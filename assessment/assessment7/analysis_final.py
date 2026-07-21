import logging
from pathlib import Path
from typing import Dict, Tuple

import matplotlib.pyplot as plt
import numpy as np
import pandas as pd


class FundPortfolio:
    """Mutual fund portfolio performance and risk analysis engine."""

    def __init__(self, base_path: Path) -> None:
        self.base_path = base_path
        self.input_dir = self.base_path / "new_csv"
        self.report_dir = self.base_path / "reports"
        self.chart_dir = self.base_path / "charts"
        self.log_dir = self.base_path / "logs"

        self.report_dir.mkdir(parents=True, exist_ok=True)
        self.chart_dir.mkdir(parents=True, exist_ok=True)
        self.log_dir.mkdir(parents=True, exist_ok=True)

        self._configure_logging()

        self.investors = pd.DataFrame()
        self.funds = pd.DataFrame()
        self.transactions = pd.DataFrame()
        self.nav_history = pd.DataFrame()
        self.merged = pd.DataFrame()

    def _configure_logging(self) -> None:
        log_file = self.log_dir / "analysis.log"
        logging.basicConfig(
            level=logging.INFO,
            format="%(asctime)s | %(levelname)s | %(message)s",
            handlers=[
                logging.FileHandler(log_file, mode="w", encoding="utf-8"),
                logging.StreamHandler(),
            ],
        )

    def safe_read_csv(self, filename: str) -> pd.DataFrame:
        file_path = self.input_dir / filename
        try:
            df = pd.read_csv(file_path)
            logging.info("Loaded %s with %d rows", filename, len(df))
            return df
        except FileNotFoundError:
            logging.error("File not found: %s", file_path)
        except pd.errors.ParserError:
            logging.error("Corrupted/invalid CSV format: %s", file_path)
        except UnicodeDecodeError:
            logging.error("Encoding issue while reading: %s", file_path)
        except Exception as ex:
            logging.error("Unexpected error reading %s: %s", file_path, ex)
        return pd.DataFrame()

    @staticmethod
    def _to_numeric(df: pd.DataFrame, cols) -> pd.DataFrame:
        for col in cols:
            if col in df.columns:
                df[col] = pd.to_numeric(df[col], errors="coerce")
        return df

    @staticmethod
    def _to_datetime(df: pd.DataFrame, cols) -> pd.DataFrame:
        for col in cols:
            if col in df.columns:
                df[col] = pd.to_datetime(df[col], errors="coerce")
        return df

    def _standardize_schema(self) -> None:
        if not self.investors.empty:
            self.investors = self.investors.rename(columns={"State": "City"})
            if "AnnualIncome" not in self.investors.columns:
                self.investors["AnnualIncome"] = np.nan
            if "RiskProfile" not in self.investors.columns:
                if "InvestorType" in self.investors.columns:
                    risk_map = {
                        "retail": "Moderate",
                        "hni": "High",
                        "institutional": "Low",
                    }
                    mapped = self.investors["InvestorType"].astype(str).str.lower().map(risk_map)
                    self.investors["RiskProfile"] = mapped.fillna("Moderate")
                else:
                    self.investors["RiskProfile"] = "Moderate"

        if not self.funds.empty:
            self.funds = self.funds.rename(columns={"AMC": "FundManager"})
            if "ExpenseRatio" not in self.funds.columns:
                self.funds["ExpenseRatio"] = np.nan
            if "Benchmark" not in self.funds.columns:
                self.funds["Benchmark"] = np.nan

        if not self.transactions.empty:
            self.transactions = self.transactions.rename(
                columns={
                    "UnitsPurchased": "Units",
                    "PurchaseNAV": "NAV",
                    "PurchaseDate": "TransactionDate",
                }
            )
            if "TransactionType" not in self.transactions.columns:
                self.transactions["TransactionType"] = "Purchase"
            if "Amount" not in self.transactions.columns and {"Units", "NAV"}.issubset(self.transactions.columns):
                units_num = pd.to_numeric(self.transactions["Units"], errors="coerce")
                nav_num = pd.to_numeric(self.transactions["NAV"], errors="coerce")
                self.transactions["Amount"] = units_num * nav_num

    def load_data(self) -> None:
        self.investors = self.safe_read_csv("investors.csv")
        self.funds = self.safe_read_csv("funds.csv")
        self.transactions = self.safe_read_csv("transactions.csv")
        self.nav_history = self.safe_read_csv("nav_history.csv")

        self._standardize_schema()

        self.investors = self._to_numeric(self.investors, ["Age", "AnnualIncome"])
        self.funds = self._to_numeric(self.funds, ["ExpenseRatio"])
        self.transactions = self._to_numeric(self.transactions, ["Units", "NAV", "Amount"])

        self.transactions = self._to_datetime(self.transactions, ["TransactionDate"])
        self.nav_history = self._to_datetime(self.nav_history, ["Date"])
        self.nav_history = self._to_numeric(self.nav_history, ["NAV"])

    def clean_missing_data(self) -> None:
        if not self.investors.empty:
            if "AnnualIncome" in self.investors.columns:
                income_median = self.investors["AnnualIncome"].median()
                self.investors["AnnualIncome"] = self.investors["AnnualIncome"].fillna(income_median)
            if "RiskProfile" in self.investors.columns:
                self.investors["RiskProfile"] = self.investors["RiskProfile"].fillna("Moderate")

        if not self.funds.empty and "ExpenseRatio" in self.funds.columns:
            expense_mean = self.funds["ExpenseRatio"].mean()
            self.funds["ExpenseRatio"] = self.funds["ExpenseRatio"].fillna(expense_mean)

        if not self.nav_history.empty and {"FundID", "Date", "NAV"}.issubset(self.nav_history.columns):
            self.nav_history = self.nav_history.sort_values(["FundID", "Date"])
            self.nav_history["NAV"] = self.nav_history.groupby("FundID")["NAV"].ffill()
            overall_nav_median = self.nav_history["NAV"].median()
            self.nav_history["NAV"] = self.nav_history["NAV"].fillna(overall_nav_median)

        logging.info("Missing data handling completed")

    def remove_duplicates(self) -> None:
        if not self.transactions.empty:
            before = len(self.transactions)
            self.transactions = self.transactions.drop_duplicates()
            removed = before - len(self.transactions)
            logging.info("Removed %d duplicate transaction rows", removed)

    def remove_outliers(self) -> None:
        if not self.transactions.empty and "Amount" in self.transactions.columns:
            cutoff = self.transactions["Amount"].quantile(0.99)
            before = len(self.transactions)
            self.transactions = self.transactions[self.transactions["Amount"] <= cutoff]
            logging.info(
                "Removed %d transaction outliers (Amount > 99th percentile)",
                before - len(self.transactions),
            )

        if not self.nav_history.empty and {"FundID", "Date", "NAV"}.issubset(self.nav_history.columns):
            self.nav_history = self.nav_history.sort_values(["FundID", "Date"])
            self.nav_history["NavDiff"] = self.nav_history.groupby("FundID")["NAV"].diff()
            mean_diff = self.nav_history["NavDiff"].mean(skipna=True)
            std_diff = self.nav_history["NavDiff"].std(skipna=True)

            if pd.notna(mean_diff) and pd.notna(std_diff) and std_diff > 0:
                lower_limit = mean_diff - (3 * std_diff)
                upper_limit = mean_diff + (3 * std_diff)
                before = len(self.nav_history)
                self.nav_history = self.nav_history[
                    (
                        (self.nav_history["NavDiff"] >= lower_limit)
                        & (self.nav_history["NavDiff"] <= upper_limit)
                    )
                    | (self.nav_history["NavDiff"].isna())
                ]
                logging.info(
                    "Removed %d NAV outliers (change > 3 std dev)",
                    before - len(self.nav_history),
                )
            self.nav_history = self.nav_history.drop(columns=["NavDiff"], errors="ignore")

    def merge_data(self) -> None:
        if self.transactions.empty:
            logging.warning("Transactions data is empty; merged dataset cannot be formed")
            self.merged = pd.DataFrame()
            return

        merged = self.transactions.copy()

        if not self.investors.empty and "InvestorID" in merged.columns and "InvestorID" in self.investors.columns:
            merged = merged.merge(self.investors, on="InvestorID", how="left", suffixes=("", "_inv"))

        if not self.funds.empty and "FundID" in merged.columns and "FundID" in self.funds.columns:
            merged = merged.merge(self.funds, on="FundID", how="left", suffixes=("", "_fund"))

        if not self.nav_history.empty and {"FundID", "Date", "NAV"}.issubset(self.nav_history.columns):
            latest_nav = (
                self.nav_history.sort_values("Date").groupby("FundID", as_index=False).tail(1)[["FundID", "NAV"]]
                .rename(columns={"NAV": "LatestNAV"})
            )
            merged = merged.merge(latest_nav, on="FundID", how="left")

        self.merged = merged
        logging.info("Merged data created with %d rows", len(self.merged))

    def _compute_fund_returns(self) -> pd.DataFrame:
        if self.nav_history.empty or not {"FundID", "Date", "NAV"}.issubset(self.nav_history.columns):
            return pd.DataFrame(columns=["FundID", "StartNAV", "EndNAV", "FundReturnPct"])

        ordered = self.nav_history.sort_values(["FundID", "Date"])
        start_nav = ordered.groupby("FundID", as_index=False).first()[["FundID", "NAV"]].rename(columns={"NAV": "StartNAV"})
        end_nav = ordered.groupby("FundID", as_index=False).last()[["FundID", "NAV"]].rename(columns={"NAV": "EndNAV"})

        ret = start_nav.merge(end_nav, on="FundID", how="inner")
        ret["FundReturnPct"] = np.where(
            ret["StartNAV"] > 0,
            (ret["EndNAV"] - ret["StartNAV"]) / ret["StartNAV"] * 100,
            np.nan,
        )
        return ret

    def compute_numpy_metrics(self) -> Dict[str, float]:
        metrics = {}

        metrics["MeanInvestmentAmount"] = float(self.transactions["Amount"].mean()) if "Amount" in self.transactions else np.nan
        metrics["MedianInvestorIncome"] = float(self.investors["AnnualIncome"].median()) if "AnnualIncome" in self.investors else np.nan
        metrics["StdDevNAV"] = float(self.nav_history["NAV"].std()) if "NAV" in self.nav_history else np.nan

        fund_returns = self._compute_fund_returns()
        if not fund_returns.empty and "FundReturnPct" in fund_returns.columns:
            returns = fund_returns["FundReturnPct"].dropna().to_numpy()
            metrics["FundReturnP90"] = float(np.percentile(returns, 90)) if len(returns) else np.nan
            metrics["FundReturnP95"] = float(np.percentile(returns, 95)) if len(returns) else np.nan
        else:
            metrics["FundReturnP90"] = np.nan
            metrics["FundReturnP95"] = np.nan

        if not self.merged.empty and {"AnnualIncome", "Amount"}.issubset(self.merged.columns):
            corr_df = self.merged[["AnnualIncome", "Amount"]].dropna()
            metrics["IncomeInvestmentCorrelation"] = (
                float(np.corrcoef(corr_df["AnnualIncome"], corr_df["Amount"])[0, 1]) if len(corr_df) > 1 else np.nan
            )
        else:
            metrics["IncomeInvestmentCorrelation"] = np.nan

        if not self.nav_history.empty and {"Date", "NAV"}.issubset(self.nav_history.columns):
            avg_daily_nav = self.nav_history.groupby("Date")["NAV"].mean().mean()
            metrics["AverageDailyNAV"] = float(avg_daily_nav)
        else:
            metrics["AverageDailyNAV"] = np.nan

        return metrics

    def investor_portfolio_value(self) -> pd.DataFrame:
        if self.merged.empty:
            return pd.DataFrame(columns=["InvestorID", "PortfolioValue"])

        temp = self.merged.copy()
        nav_col = "LatestNAV" if "LatestNAV" in temp.columns else ("NAV" if "NAV" in temp.columns else None)
        if "Units" in temp.columns and nav_col is not None:
            temp["Value"] = temp["Units"].fillna(0) * temp[nav_col].fillna(0)
        elif "Amount" in temp.columns:
            temp["Value"] = temp["Amount"].fillna(0)
        else:
            temp["Value"] = 0

        investor_values = temp.groupby("InvestorID", as_index=False)["Value"].sum().rename(columns={"Value": "PortfolioValue"})

        if "InvestorName" in temp.columns:
            names = temp[["InvestorID", "InvestorName"]].drop_duplicates()
            investor_values = investor_values.merge(names, on="InvestorID", how="left")

        return investor_values.sort_values("PortfolioValue", ascending=False)

    def identify_investors(self) -> Dict[str, pd.DataFrame]:
        results = {}

        top20 = self.investor_portfolio_value().head(20)
        results["Top20Investors"] = top20

        if self.merged.empty:
            results["InvestmentOver10Lakhs"] = pd.DataFrame()
            results["HighRiskInvestors"] = pd.DataFrame()
            results["MoreThan10Transactions"] = pd.DataFrame()
            results["IncomeOver15Lakhs"] = pd.DataFrame()
            return results

        results["InvestmentOver10Lakhs"] = (
            self.merged.groupby("InvestorID", as_index=False)["Amount"].sum().query("Amount > 1000000")
            if "Amount" in self.merged.columns
            else pd.DataFrame()
        )

        results["HighRiskInvestors"] = (
            self.merged[self.merged.get("RiskProfile", pd.Series(dtype=str)) == "High"]
            [[c for c in ["InvestorID", "InvestorName", "RiskProfile"] if c in self.merged.columns]]
            .drop_duplicates()
            if "RiskProfile" in self.merged.columns
            else pd.DataFrame()
        )

        results["MoreThan10Transactions"] = (
            self.merged.groupby("InvestorID", as_index=False)["TransactionID"].count()
            .rename(columns={"TransactionID": "TransactionCount"})
            .query("TransactionCount > 10")
            if "TransactionID" in self.merged.columns
            else pd.DataFrame()
        )

        results["IncomeOver15Lakhs"] = (
            self.merged[self.merged.get("AnnualIncome", pd.Series(dtype=float)) > 1500000]
            [[c for c in ["InvestorID", "InvestorName", "AnnualIncome"] if c in self.merged.columns]]
            .drop_duplicates()
            if "AnnualIncome" in self.merged.columns
            else pd.DataFrame()
        )

        return results

    def fund_analysis(self) -> Dict[str, pd.DataFrame]:
        results = {}

        fund_returns = self._compute_fund_returns()
        fund_meta_cols = [c for c in ["FundID", "FundName", "Category", "ExpenseRatio"] if c in self.funds.columns]

        if fund_meta_cols and not self.funds.empty:
            fund_returns = fund_returns.merge(self.funds[fund_meta_cols], on="FundID", how="left")

        if not fund_returns.empty and "FundReturnPct" in fund_returns.columns:
            results["BestPerformingFund"] = fund_returns.nlargest(1, "FundReturnPct")
            results["WorstPerformingFund"] = fund_returns.nsmallest(1, "FundReturnPct")
        else:
            results["BestPerformingFund"] = pd.DataFrame()
            results["WorstPerformingFund"] = pd.DataFrame()

        results["HighestExpenseRatio"] = (
            self.funds.nlargest(1, "ExpenseRatio") if not self.funds.empty and "ExpenseRatio" in self.funds.columns else pd.DataFrame()
        )

        if not self.transactions.empty and "Amount" in self.transactions.columns:
            aum = self.transactions.groupby("FundID", as_index=False)["Amount"].sum().rename(columns={"Amount": "AUM"})
            if "FundName" in self.funds.columns:
                aum = aum.merge(self.funds[["FundID", "FundName"]], on="FundID", how="left")
            results["HighestAUM"] = aum.nlargest(1, "AUM")
        else:
            results["HighestAUM"] = pd.DataFrame()

        if not self.transactions.empty and "FundID" in self.transactions.columns:
            popularity = (
                self.transactions.groupby("FundID", as_index=False)["InvestorID"]
                .nunique()
                .rename(columns={"InvestorID": "UniqueInvestors"})
            )
            if "FundName" in self.funds.columns:
                popularity = popularity.merge(self.funds[["FundID", "FundName"]], on="FundID", how="left")
            results["MostPopularFund"] = popularity.nlargest(1, "UniqueInvestors")
        else:
            results["MostPopularFund"] = pd.DataFrame()

        results["FundReturns"] = fund_returns
        return results

    def compute_finance_metrics(self) -> Tuple[Dict[str, float], pd.DataFrame, pd.DataFrame, pd.DataFrame]:
        metrics = {}

        if self.merged.empty:
            return metrics, pd.DataFrame(), pd.DataFrame(), pd.DataFrame()

        temp = self.merged.copy()
        nav_col = "LatestNAV" if "LatestNAV" in temp.columns else ("NAV" if "NAV" in temp.columns else None)

        if "Units" in temp.columns and nav_col is not None:
            temp["CurrentValue"] = temp["Units"].fillna(0) * temp[nav_col].fillna(0)
        elif "Amount" in temp.columns:
            temp["CurrentValue"] = temp["Amount"].fillna(0)
        else:
            temp["CurrentValue"] = 0

        if "Amount" in temp.columns:
            temp["CostBasis"] = temp["Amount"].fillna(0)
        elif "Units" in temp.columns and "NAV" in temp.columns:
            temp["CostBasis"] = temp["Units"].fillna(0) * temp["NAV"].fillna(0)
        else:
            temp["CostBasis"] = 0

        temp["ProfitLoss"] = temp["CurrentValue"] - temp["CostBasis"]

        total_portfolio_value = temp["CurrentValue"].sum()
        total_cost = temp["CostBasis"].sum()
        absolute_return = total_portfolio_value - total_cost
        portfolio_return_pct = (absolute_return / total_cost * 100) if total_cost > 0 else np.nan

        metrics["TotalPortfolioValue"] = float(total_portfolio_value)
        metrics["PortfolioReturnPct"] = float(portfolio_return_pct) if pd.notna(portfolio_return_pct) else np.nan
        metrics["AbsoluteReturn"] = float(absolute_return)

        if "TransactionDate" in temp.columns and temp["TransactionDate"].notna().any():
            days = (temp["TransactionDate"].max() - temp["TransactionDate"].min()).days
            years = max(days / 365.25, 1 / 365.25)
        else:
            years = np.nan

        if pd.notna(years) and years > 0 and total_cost > 0:
            cagr = ((total_portfolio_value / total_cost) ** (1 / years) - 1) * 100
            annualized_return = ((1 + portfolio_return_pct / 100) ** (1 / years) - 1) * 100
        else:
            cagr = np.nan
            annualized_return = np.nan

        metrics["CAGR"] = float(cagr) if pd.notna(cagr) else np.nan
        metrics["AnnualizedReturn"] = float(annualized_return) if pd.notna(annualized_return) else np.nan

        if "FundID" in temp.columns:
            fund_weights = temp.groupby("FundID")["CurrentValue"].sum()
            total_value = fund_weights.sum()
            if total_value > 0:
                weights = fund_weights / total_value
                hhi = np.sum(np.square(weights.values))
                diversification_score = float((1 - hhi) * 100)
            else:
                diversification_score = np.nan
        else:
            diversification_score = np.nan

        metrics["PortfolioDiversificationScore"] = diversification_score

        if "TransactionDate" in temp.columns and temp["TransactionDate"].notna().any():
            latest_date = temp["TransactionDate"].max()
            temp["HoldingDays"] = (latest_date - temp["TransactionDate"]).dt.days
            metrics["AverageHoldingPeriodDays"] = float(temp["HoldingDays"].mean())
        else:
            metrics["AverageHoldingPeriodDays"] = np.nan

        if "ExpenseRatio" in temp.columns:
            exp = temp["ExpenseRatio"].fillna(temp["ExpenseRatio"].mean()) / 100.0
            metrics["ExpenseRatioImpact"] = float((temp["CurrentValue"] * exp).sum())
        else:
            metrics["ExpenseRatioImpact"] = np.nan

        if "Date" in self.nav_history.columns and "NAV" in self.nav_history.columns and not self.nav_history.empty:
            nav_daily = self.nav_history.sort_values("Date").groupby("Date")["NAV"].mean()
            daily_returns = nav_daily.pct_change().dropna()
            if len(daily_returns) > 1 and daily_returns.std() > 0:
                sharpe = (daily_returns.mean() / daily_returns.std()) * np.sqrt(252)
                metrics["SharpeRatioSimplified"] = float(sharpe)
            else:
                metrics["SharpeRatioSimplified"] = np.nan
        else:
            metrics["SharpeRatioSimplified"] = np.nan

        category_investment = pd.DataFrame()
        if "Category" in temp.columns:
            category_investment = temp.groupby("Category", as_index=False)["CostBasis"].sum()
            total_cat = category_investment["CostBasis"].sum()
            category_investment["CategoryInvestmentPct"] = np.where(
                total_cat > 0,
                category_investment["CostBasis"] / total_cat * 100,
                np.nan,
            )

        fund_allocation = pd.DataFrame()
        if "FundID" in temp.columns:
            fund_allocation = temp.groupby("FundID", as_index=False)["CurrentValue"].sum()
            if "FundName" in temp.columns:
                names = temp[["FundID", "FundName"]].drop_duplicates()
                fund_allocation = fund_allocation.merge(names, on="FundID", how="left")
            total_fund_alloc = fund_allocation["CurrentValue"].sum()
            fund_allocation["FundAllocationPct"] = np.where(
                total_fund_alloc > 0,
                fund_allocation["CurrentValue"] / total_fund_alloc * 100,
                np.nan,
            )

        investor_pl = temp.groupby("InvestorID", as_index=False)[["CostBasis", "CurrentValue", "ProfitLoss"]].sum()
        if "InvestorName" in temp.columns:
            names = temp[["InvestorID", "InvestorName"]].drop_duplicates()
            investor_pl = investor_pl.merge(names, on="InvestorID", how="left")

        return metrics, category_investment, fund_allocation, investor_pl

    def create_charts(
        self,
        fund_allocation: pd.DataFrame,
        top_investors: pd.DataFrame,
        fund_returns: pd.DataFrame,
        category_investment: pd.DataFrame,
    ) -> None:
        plt.style.use("ggplot")

        if not fund_allocation.empty and "CurrentValue" in fund_allocation.columns:
            labels = (
                fund_allocation["FundName"].fillna(fund_allocation["FundID"]).astype(str)
                if "FundName" in fund_allocation.columns
                else fund_allocation["FundID"].astype(str)
            )
            plt.figure(figsize=(8, 8))
            plt.pie(fund_allocation["CurrentValue"], labels=labels, autopct="%1.1f%%", startangle=140)
            plt.title("Portfolio Allocation")
            plt.tight_layout()
            plt.savefig(self.chart_dir / "portfolio_allocation_pie.png")
            plt.close()

        if not self.transactions.empty and {"FundID", "Amount"}.issubset(self.transactions.columns):
            fw = self.transactions.groupby("FundID", as_index=False)["Amount"].sum().sort_values("Amount", ascending=False)
            if "FundName" in self.funds.columns:
                fw = fw.merge(self.funds[["FundID", "FundName"]], on="FundID", how="left")
                xvals = fw["FundName"].fillna(fw["FundID"])
            else:
                xvals = fw["FundID"]
            plt.figure(figsize=(10, 5))
            plt.bar(xvals, fw["Amount"])
            plt.title("Fund-wise Investment")
            plt.xticks(rotation=60, ha="right")
            plt.tight_layout()
            plt.savefig(self.chart_dir / "fund_wise_investment_bar.png")
            plt.close()

        if not self.transactions.empty and {"TransactionDate", "Amount"}.issubset(self.transactions.columns):
            monthly = self.transactions.dropna(subset=["TransactionDate"]).copy()
            if not monthly.empty:
                monthly["Month"] = monthly["TransactionDate"].dt.to_period("M").dt.to_timestamp()
                mt = monthly.groupby("Month", as_index=False)["Amount"].sum()
                plt.figure(figsize=(10, 5))
                plt.plot(mt["Month"], mt["Amount"], marker="o")
                plt.title("Monthly Investment Trend")
                plt.tight_layout()
                plt.savefig(self.chart_dir / "monthly_investment_trend_line.png")
                plt.close()

        if not fund_returns.empty and "Category" in fund_returns.columns and "FundReturnPct" in fund_returns.columns:
            cat_returns = fund_returns.groupby("Category", as_index=False)["FundReturnPct"].mean()
            plt.figure(figsize=(9, 5))
            plt.bar(cat_returns["Category"], cat_returns["FundReturnPct"])
            plt.title("Category-wise Returns")
            plt.xticks(rotation=45, ha="right")
            plt.tight_layout()
            plt.savefig(self.chart_dir / "category_wise_returns_bar.png")
            plt.close()

        if not self.nav_history.empty and {"Date", "NAV"}.issubset(self.nav_history.columns):
            nav_line = self.nav_history.groupby("Date", as_index=False)["NAV"].mean().sort_values("Date")
            plt.figure(figsize=(10, 5))
            plt.plot(nav_line["Date"], nav_line["NAV"])
            plt.title("NAV Movement")
            plt.tight_layout()
            plt.savefig(self.chart_dir / "nav_movement_line.png")
            plt.close()

        if not top_investors.empty and "PortfolioValue" in top_investors.columns:
            t10 = top_investors.head(10).copy()
            yvals = t10["InvestorName"].fillna(t10["InvestorID"]) if "InvestorName" in t10.columns else t10["InvestorID"]
            plt.figure(figsize=(10, 6))
            plt.barh(yvals.astype(str), t10["PortfolioValue"])
            plt.title("Top 10 Investors")
            plt.tight_layout()
            plt.savefig(self.chart_dir / "top10_investors_horizontal_bar.png")
            plt.close()

        logging.info("Charts generated in %s", self.chart_dir)

    def export_reports(
        self,
        numpy_metrics: Dict[str, float],
        investor_sets: Dict[str, pd.DataFrame],
        fund_results: Dict[str, pd.DataFrame],
        finance_metrics: Dict[str, float],
        category_investment: pd.DataFrame,
        fund_allocation: pd.DataFrame,
        investor_pl: pd.DataFrame,
    ) -> None:
        pd.DataFrame([numpy_metrics]).to_csv(self.report_dir / "numpy_metrics.csv", index=False)
        pd.DataFrame([finance_metrics]).to_csv(self.report_dir / "finance_metrics.csv", index=False)

        for key, df in investor_sets.items():
            if isinstance(df, pd.DataFrame):
                df.to_csv(self.report_dir / f"{key}.csv", index=False)

        for key, df in fund_results.items():
            if isinstance(df, pd.DataFrame):
                df.to_csv(self.report_dir / f"{key}.csv", index=False)

        category_investment.to_csv(self.report_dir / "category_investment_pct.csv", index=False)
        fund_allocation.to_csv(self.report_dir / "fund_allocation_pct.csv", index=False)
        investor_pl.to_csv(self.report_dir / "investor_profit_loss.csv", index=False)
        self.merged.to_csv(self.report_dir / "merged_dataset.csv", index=False)

        summary = {
            "numpy_metrics": numpy_metrics,
            "finance_metrics": finance_metrics,
            "report_files": sorted([p.name for p in self.report_dir.glob("*.csv")]),
            "chart_files": sorted([p.name for p in self.chart_dir.glob("*.png")]),
        }
        pd.Series(summary).to_json(self.report_dir / "summary.json", indent=2)

        logging.info("Reports exported to %s", self.report_dir)

    def run(self) -> None:
        logging.info("Analysis execution started")
        self.load_data()
        self.clean_missing_data()
        self.remove_duplicates()
        self.remove_outliers()
        self.merge_data()

        numpy_metrics = self.compute_numpy_metrics()
        investor_sets = self.identify_investors()
        fund_results = self.fund_analysis()
        finance_metrics, category_investment, fund_allocation, investor_pl = self.compute_finance_metrics()

        top20 = investor_sets.get("Top20Investors", pd.DataFrame())
        self.create_charts(fund_allocation, top20, fund_results.get("FundReturns", pd.DataFrame()), category_investment)

        self.export_reports(
            numpy_metrics,
            investor_sets,
            fund_results,
            finance_metrics,
            category_investment,
            fund_allocation,
            investor_pl,
        )

        logging.info("Analysis execution completed successfully")


def main() -> None:
    base_path = Path(__file__).resolve().parent
    portfolio = FundPortfolio(base_path)
    portfolio.run()


if __name__ == "__main__":
    main()
