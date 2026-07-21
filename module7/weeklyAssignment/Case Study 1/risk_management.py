from pathlib import Path
import json

import numpy as np
import pandas as pd


########################
# CASE STUDY 1 OVERVIEW
########################

'''
Business Scenario:
Abank has a large loan portfolio. The goal is to identify risky customers,
calculate portfolio statistics, and generate automated reports.

Implemented Requirements:
- Python: multi-file read, exception handling, functions, OOP with Loan class
- NumPy: mean, median, percentile, correlation, standard deviation
- Pandas: merge, filters, missing-data handling, outlier removal
- Finance Metrics: DTI, utilization, default %, NPA %, average EMI, expected loss
- Automation: risk_report.xlsx, high_risk_customers.csv, summary.json
'''


########################
# PART 1 - OOP MODEL
########################

'''
Part 1 Description:
Define the Loan class used to compute per-loan risk and outstanding amount.
'''
class Loan:
	def __init__(self, loan_id, customer_id, loan_amount, paid_emis, tenure, default_flag, credit_score):
		self.loan_id = loan_id
		self.customer_id = customer_id
		self.loan_amount = float(loan_amount)
		self.paid_emis = int(paid_emis)
		self.tenure = int(tenure)
		self.default_flag = int(default_flag)
		self.credit_score = int(credit_score)

	def outstanding_amount(self):
		if self.tenure <= 0:
			return self.loan_amount
		return self.loan_amount * (1 - (self.paid_emis / self.tenure))

	def is_high_risk(self):
		return self.default_flag == 1 or self.credit_score < 650


########################
# PART 2 - FILE LOADING
########################

'''

Part 2 Description:
Read multiple CSV files with exception handling for corrupted or invalid files.
'''
def read_csv_file(file_path, required_columns):
	try:
		df = pd.read_csv(file_path)
	except FileNotFoundError:
		raise FileNotFoundError(f"File not found: {file_path.name}")
	except pd.errors.EmptyDataError:
		raise ValueError(f"File is empty/corrupted: {file_path.name}")
	except pd.errors.ParserError:
		raise ValueError(f"CSV format corrupted: {file_path.name}")

	missing = [col for col in required_columns if col not in df.columns]
	if missing:
		raise ValueError(f"Missing columns {missing} in {file_path.name}")

	return df


########################
# PART 3 - INPUT DATASET PREP
########################

'''
Part 3 Description:
Load customers, loans, and credit score files from the case-study folder.
'''
def load_input_files(base_path):
	customers = read_csv_file(
		base_path / "customers.csv",
		["CustomerID", "Age", "Salary", "City"],
	)
	loans = read_csv_file(
		base_path / "loans.csv",
		["LoanID", "CustomerID", "LoanAmount", "Tenure", "PaidEMIs", "DefaultFlag"],
	)

	credit_file = base_path / "credit_score.csv"
	if not credit_file.exists():
		credit_file = base_path / "credit_scores.csv"

	credit_scores = read_csv_file(
		credit_file,
		["CustomerID", "CreditScore"],
	)

	return customers, loans, credit_scores


########################
# PART 4 - OBJECT CONVERSION
########################

'''
Part 4 Description:
Convert merged DataFrame rows into Loan objects.
'''
def build_loan_objects(merged_df):
	loan_objects = []
	for row in merged_df.itertuples(index=False):
		loan_objects.append(
			Loan(
				loan_id=row.LoanID,
				customer_id=row.CustomerID,
				loan_amount=row.LoanAmount,
				paid_emis=row.PaidEMIs,
				tenure=row.Tenure,
				default_flag=row.DefaultFlag,
				credit_score=row.CreditScore,
			)
		)
	return loan_objects


########################
# PART 5 - PORTFOLIO METRICS
########################

'''
Part 5 Description:
Calculate portfolio-level risk and exposure metrics using Loan objects.
'''
def calculate_portfolio_metrics(loans):
	total_loans = len(loans)
	total_exposure = sum(loan.loan_amount for loan in loans)
	default_count = sum(loan.default_flag for loan in loans)
	high_risk_count = sum(1 for loan in loans if loan.is_high_risk())
	outstanding_exposure = sum(loan.outstanding_amount() for loan in loans)

	return {
		"Total Loans": total_loans,
		"Total Exposure": round(total_exposure, 2),
		"Outstanding Exposure": round(outstanding_exposure, 2),
		"Default Rate (%)": round((default_count / total_loans) * 100, 2) if total_loans else 0,
		"High Risk Customers": high_risk_count,
	}


########################
# PART 6 - NUMPY STATISTICS
########################

'''
Part 6 Description:
Calculate required NumPy statistics from merged loan portfolio data.
'''
def calculate_numpy_statistics(merged_df):
	loan_amounts = merged_df["LoanAmount"].to_numpy(dtype=float)
	salaries = merged_df["Salary"].to_numpy(dtype=float)
	interest_rates = merged_df["InterestRate"].to_numpy(dtype=float)

	mean_loan_amount = float(np.mean(loan_amounts))
	median_salary = float(np.median(salaries))
	percentile_interest_rate = float(np.percentile(interest_rates, 75))
	correlation_salary_loan = float(np.corrcoef(salaries, loan_amounts)[0, 1])
	std_loan_amount = float(np.std(loan_amounts))

	return {
		"Mean Loan Amount": round(mean_loan_amount, 2),
		"Median Salary": round(median_salary, 2),
		"75th Percentile Interest Rate": round(percentile_interest_rate, 2),
		"Correlation (Salary vs LoanAmount)": round(correlation_salary_loan, 4),
		"Standard Deviation (LoanAmount)": round(std_loan_amount, 2),
	}


########################
# PART 7 - FINANCE METRICS
########################

'''
Part 7 Description:
Calculate finance metrics: DTI, utilization, default %, NPA %, average EMI,
and expected loss.
'''
def calculate_finance_metrics(merged_df):
	total_loans = len(merged_df)
	total_exposure = float(merged_df["LoanAmount"].sum())
	total_outstanding = float(
		(merged_df["LoanAmount"] * (1 - (merged_df["PaidEMIs"] / merged_df["Tenure"]))).sum()
	)

	dti_ratio = np.where(merged_df["Salary"] > 0, merged_df["LoanAmount"] / merged_df["Salary"], np.nan)
	avg_dti_ratio = float(np.nanmean(dti_ratio)) if len(dti_ratio) else 0.0

	loan_utilization = (total_outstanding / total_exposure * 100) if total_exposure else 0.0
	default_percent = (
		float((merged_df["DefaultFlag"] == 1).sum()) / total_loans * 100 if total_loans else 0.0
	)

	npa_outstanding = float(
		(
			merged_df.loc[merged_df["DefaultFlag"] == 1, "LoanAmount"]
			* (1 - (merged_df.loc[merged_df["DefaultFlag"] == 1, "PaidEMIs"] / merged_df.loc[merged_df["DefaultFlag"] == 1, "Tenure"]))
		).sum()
	)
	npa_percent = (npa_outstanding / total_outstanding * 100) if total_outstanding else 0.0

	average_emi = float(merged_df["EMI"].mean()) if total_loans else 0.0

	# Expected Loss = PD * LGD * EAD (LGD assumed at 45% for portfolio-level estimate)
	pd = default_percent / 100
	lgd = 0.45
	ead = total_outstanding
	expected_loss = pd * lgd * ead

	return {
		"Debt-to-Income Ratio": round(avg_dti_ratio, 4),
		"Loan Utilization (%)": round(loan_utilization, 2),
		"Default %": round(default_percent, 2),
		"NPA %": round(npa_percent, 2),
		"Average EMI": round(average_emi, 2),
		"Expected Loss": round(expected_loss, 2),
	}


########################
# PART 8 - DATA PROCESSING
########################

'''
Part 8 Description:
Merge datasets, handle missing values, and remove outliers.
'''
def merge_customers_loans_credit_score(customers_df, loans_df, credit_scores_df):
	merged_df = pd.merge(loans_df, customers_df, on="CustomerID", how="inner")
	merged_df = pd.merge(merged_df, credit_scores_df, on="CustomerID", how="inner")
	return merged_df


def handle_missing_data(merged_df):
	cleaned_df = merged_df.copy()

	cleaned_df["Salary"] = cleaned_df["Salary"].fillna(cleaned_df["Salary"].median())
	cleaned_df["CreditScore"] = cleaned_df["CreditScore"].fillna(cleaned_df["CreditScore"].mean())
	cleaned_df["InterestRate"] = cleaned_df["InterestRate"].ffill()

	return cleaned_df


def remove_loan_amount_outliers(merged_df):
	percentile_99 = merged_df["LoanAmount"].quantile(0.99)
	return merged_df[merged_df["LoanAmount"] <= percentile_99].copy()


########################
# PART 9 - RISK FILTERING
########################

'''
Part 9 Description:
Find top 20 risky customers using business risk rules.

Rules:
- CreditScore < 650
- Salary < 60000
- LoanAmount > 10 Lakhs
- DefaultFlag = 1
'''
def find_top_20_risky_customers(merged_df):
	risky_df = merged_df.copy()

	risky_df["Rule_CreditScore_lt_650"] = risky_df["CreditScore"] < 650
	risky_df["Rule_Salary_lt_60000"] = risky_df["Salary"] < 60000
	risky_df["Rule_Loan_gt_10Lakhs"] = risky_df["LoanAmount"] > 1000000
	risky_df["Rule_DefaultFlag_eq_1"] = risky_df["DefaultFlag"] == 1

	risky_df["RiskRuleCount"] = (
		risky_df["Rule_CreditScore_lt_650"].astype(int)
		+ risky_df["Rule_Salary_lt_60000"].astype(int)
		+ risky_df["Rule_Loan_gt_10Lakhs"].astype(int)
		+ risky_df["Rule_DefaultFlag_eq_1"].astype(int)
	)

	risky_df = risky_df[risky_df["RiskRuleCount"] > 0]
	risky_df = risky_df.sort_values(
		by=["RiskRuleCount", "DefaultFlag", "CreditScore", "LoanAmount"],
		ascending=[False, False, True, False],
	)

	return risky_df.head(20)[
		[
			"CustomerID",
			"LoanID",
			"CreditScore",
			"Salary",
			"LoanAmount",
			"DefaultFlag",
			"RiskRuleCount",
		]
	]


########################
# PART 10 - EXPORT REPORTS
########################

'''
Part 10 Description:
Generate export reports for risk monitoring and business reporting.

Expected Outputs:
- risk_report.xlsx
- high_risk_customers.csv
- summary.json
'''
def export_reports(base_path, merged_df, top_20_risky_df, metrics, numpy_stats, finance_metrics):
	high_risk_customers_df = merged_df[
		(merged_df["DefaultFlag"] == 1) | (merged_df["CreditScore"] < 650)
	][
		[
			"CustomerID",
			"LoanID",
			"CreditScore",
			"Salary",
			"LoanAmount",
			"DefaultFlag",
		]
	].copy()

	high_risk_customers_df.to_csv(base_path / "high_risk_customers.csv", index=False)

	with pd.ExcelWriter(base_path / "risk_report.xlsx", engine="openpyxl") as writer:
		merged_df.to_excel(writer, sheet_name="merged_data", index=False)
		top_20_risky_df.to_excel(writer, sheet_name="top_20_risky", index=False)
		high_risk_customers_df.to_excel(writer, sheet_name="high_risk_customers", index=False)

		metrics_df = pd.DataFrame(list(metrics.items()), columns=["Metric", "Value"])
		numpy_df = pd.DataFrame(list(numpy_stats.items()), columns=["Metric", "Value"])
		finance_df = pd.DataFrame(list(finance_metrics.items()), columns=["Metric", "Value"])

		metrics_df.to_excel(writer, sheet_name="portfolio_metrics", index=False)
		numpy_df.to_excel(writer, sheet_name="numpy_metrics", index=False)
		finance_df.to_excel(writer, sheet_name="finance_metrics", index=False)

	summary_payload = {
		"portfolio_metrics": metrics,
		"numpy_metrics": numpy_stats,
		"finance_metrics": finance_metrics,
		"record_counts": {
			"merged_records": int(len(merged_df)),
			"high_risk_customers": int(len(high_risk_customers_df)),
			"top_20_risky_customers": int(len(top_20_risky_df)),
		},
	}

	with open(base_path / "summary.json", "w", encoding="utf-8") as json_file:
		json.dump(summary_payload, json_file, indent=2)


########################
# PART 11 - MAIN EXECUTION
########################

'''
Part 11 Description:
Run end-to-end pipeline:
1) Load files
2) Merge and clean data
3) Calculate all metrics
4) Print outputs
5) Export report files
'''
def main():
	base_path = Path(__file__).resolve().parent

	try:
		customers, loans, credit_scores = load_input_files(base_path)
	except Exception as error:
		print(f"Error while reading files: {error}")
		return

	merged = merge_customers_loans_credit_score(customers, loans, credit_scores)
	merged = handle_missing_data(merged)
	merged = remove_loan_amount_outliers(merged)

	loan_objects = build_loan_objects(merged)
	high_risk = [loan for loan in loan_objects if loan.is_high_risk()]
	metrics = calculate_portfolio_metrics(loan_objects)
	numpy_stats = calculate_numpy_statistics(merged)
	finance_metrics = calculate_finance_metrics(merged)
	top_20_risky = find_top_20_risky_customers(merged)

	print("\nHigh-risk customers (LoanID, CustomerID):")
	for loan in high_risk:
		print(loan.loan_id, loan.customer_id)

	print("\nPortfolio Risk Metrics:")
	for key, value in metrics.items():
		print(f"{key}: {value}")

	print("\nNumPy Calculations:")
	for key, value in numpy_stats.items():
		print(f"{key}: {value}")

	print("\nFinance Metrics:")
	for key, value in finance_metrics.items():
		print(f"{key}: {value}")

	print("\nTop 20 Risky Customers (based on given conditions):")
	if top_20_risky.empty:
		print("No records found for the given risk conditions.")
	else:
		print(top_20_risky.to_string(index=False))

	# Final automation step: export all required output files.
	export_reports(base_path, merged, top_20_risky, metrics, numpy_stats, finance_metrics)
	print("\nGenerated files: risk_report.xlsx, high_risk_customers.csv, summary.json")


if __name__ == "__main__":
	main()
