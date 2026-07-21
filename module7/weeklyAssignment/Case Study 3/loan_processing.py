from pathlib import Path
import numpy as np
import pandas as pd


############################
# CASE STUDY 3 - LOAN PROCESSING
############################

'''
Beginner-friendly solution
All tasks are divided into PART sections with clear comments.
'''

############################
# PART 1 - READ DATA
############################


def read_data(base_path):
	# Read customers.csv
	customers = pd.read_csv(base_path / "customers.csv")

	# Read loan application file (support both names from task/doc and folder)
	loan_app_path = base_path / "loan_applications.csv"
	if not loan_app_path.exists():
		loan_app_path = base_path / "loan_application.csv"
	loan_applications = pd.read_csv(loan_app_path)

	# Read loan_payments.csv
	loan_payments = pd.read_csv(base_path / "loan_payments.csv")

	return customers, loan_applications, loan_payments


############################
# PART 2 - DATA CLEANING
############################


def clean_data(customers, loan_applications, loan_payments):
	# Remove duplicate rows from all files
	customers = customers.drop_duplicates().copy()
	loan_applications = loan_applications.drop_duplicates().copy()
	loan_payments = loan_payments.drop_duplicates().copy()

	# Remove duplicate Loan IDs in loan applications
	loan_applications = loan_applications.drop_duplicates(subset=["LoanID"]).copy()

	# Show missing values for checking
	print("\nMissing values before fill:")
	print("customers:\n", customers.isna().sum())
	print("loan_applications:\n", loan_applications.isna().sum())
	print("loan_payments:\n", loan_payments.isna().sum())

	# If CreditScore is not present in source file, create a simple beginner-friendly score from salary
	if "CreditScore" not in customers.columns:
		customers["CreditScore"] = np.where(
			customers["Salary"] >= 100000,
			750,
			np.where(customers["Salary"] >= 70000, 680, 620),
		)

	# Replace missing Salary with median salary
	customers["Salary"] = customers["Salary"].fillna(customers["Salary"].median())

	# Replace missing CreditScore with mean credit score
	customers["CreditScore"] = customers["CreditScore"].fillna(customers["CreditScore"].mean())

	# Convert dates to datetime
	loan_applications["ApplicationDate"] = pd.to_datetime(
		loan_applications["ApplicationDate"], errors="coerce"
	)

	# Source has LastPaymentDate, task asks PaymentDate, so rename first
	loan_payments = loan_payments.rename(columns={"LastPaymentDate": "PaymentDate"})
	loan_payments["PaymentDate"] = pd.to_datetime(loan_payments["PaymentDate"], errors="coerce")

	# Remove negative LoanAmount
	loan_applications = loan_applications[loan_applications["LoanAmount"] > 0].copy()

	# Remove invalid EMIAmount (0 or negative)
	loan_payments = loan_payments[loan_payments["EMIAmount"] > 0].copy()

	# Remove future payment dates
	today = pd.Timestamp.today().normalize()
	loan_payments = loan_payments[loan_payments["PaymentDate"] <= today].copy()

	return customers, loan_applications, loan_payments


############################
# PART 3 - MERGE DATASETS
############################


def merge_datasets(customers, loan_applications, loan_payments):
	# Normalize CustomerID in customers: C101 -> 101
	customers["CustomerIDKey"] = customers["CustomerID"].astype(str).str.extract(r"(\d+)").astype(int)

	# Normalize CustomerID in loan applications: 101 -> 101
	loan_applications["CustomerIDKey"] = (
		loan_applications["CustomerID"].astype(str).str.extract(r"(\d+)").astype(int)
	)

	# Normalize LoanID in applications: L1001 -> 1001
	loan_applications["LoanIDKey"] = (
		loan_applications["LoanID"].astype(str).str.extract(r"(\d+)").astype(int)
	)

	# Normalize LoanID in payments: L101 -> 101, then convert to 1001 format for joining
	loan_payments["LoanIDKey"] = loan_payments["LoanID"].astype(str).str.extract(r"(\d+)").astype(int)
	loan_payments["LoanIDKey"] = np.where(
		loan_payments["LoanIDKey"] < 1000,
		loan_payments["LoanIDKey"] + 900,
		loan_payments["LoanIDKey"],
	)

	# Merge customer + application data using CustomerID
	merged = pd.merge(
		loan_applications,
		customers,
		on="CustomerIDKey",
		how="inner",
		suffixes=("_App", "_Cust"),
	)

	# Merge with payments using LoanID
	merged = pd.merge(merged, loan_payments, on="LoanIDKey", how="left")

	# Rename merged LoanID columns to clear names
	merged = merged.rename(columns={"LoanID_x": "LoanID", "LoanID_y": "PaymentLoanID"})

	# Create AmountPaid for analysis
	merged["AmountPaid"] = merged["EMIAmount"] * merged["PaidEMIs"]

	# Create PaymentStatus for analysis
	merged["PaymentStatus"] = np.where(
		merged["PendingEMIs"] == 0,
		"Paid",
		np.where(merged["PaidEMIs"] > 0, "Partial", "Pending"),
	)

	# Create a clean report view with requested columns
	report_df = merged[
		[
			"CustomerName",
			"City",
			"LoanType",
			"LoanAmount",
			"CreditScore",
			"Salary",
			"LoanStatus",
			"EMIAmount",
			"PaymentStatus",
		]
	].copy()

	return merged, report_df


############################
# PART 4 - CREATE NEW COLUMNS
############################


def create_new_columns(merged):
	# Monthly Income = Salary / 12
	merged["MonthlyIncome"] = merged["Salary"] / 12

	# Debt-to-Income Ratio = Loan Amount / Salary
	merged["DebtToIncomeRatio"] = np.where(
		merged["Salary"] > 0,
		merged["LoanAmount"] / merged["Salary"],
		np.nan,
	)

	# EMI Due = EMIAmount - AmountPaid (as requested in task doc)
	merged["EMIDue"] = merged["EMIAmount"] - merged["AmountPaid"]

	# Payment Completion % = AmountPaid / EMIAmount * 100
	merged["PaymentCompletionPct"] = np.where(
		merged["EMIAmount"] > 0,
		(merged["AmountPaid"] / merged["EMIAmount"]) * 100,
		np.nan,
	)

	return merged


############################
# PART 5 - NUMPY TASKS
############################


def get_numpy_metrics(merged):
	loan_arr = merged["LoanAmount"].to_numpy(dtype=float)

	return {
		"AverageLoanAmount": round(float(np.mean(loan_arr)), 2),
		"MedianLoanAmount": round(float(np.median(loan_arr)), 2),
		"MaximumLoanAmount": round(float(np.max(loan_arr)), 2),
		"MinimumLoanAmount": round(float(np.min(loan_arr)), 2),
		"StandardDeviation": round(float(np.std(loan_arr)), 2),
		"Variance": round(float(np.var(loan_arr)), 2),
		"25thPercentileLoanAmount": round(float(np.percentile(loan_arr, 25)), 2),
		"75thPercentileLoanAmount": round(float(np.percentile(loan_arr, 75)), 2),
	}


############################
# PART 6 - PANDAS ANALYSIS
############################


def run_pandas_analysis(merged):
	# Top 10 highest loan customers
	top_10_loan_customers = merged.nlargest(10, "LoanAmount")[
		["CustomerName", "City", "LoanAmount", "LoanType"]
	]

	# Top 10 customers by salary
	top_10_salary_customers = merged.nlargest(10, "Salary")[
		["CustomerName", "City", "Salary"]
	]

	# Customers with low credit score
	low_credit_customers = merged[merged["CreditScore"] < 650][
		["CustomerName", "CreditScore", "LoanAmount", "LoanStatus"]
	]

	# Customers with loan amount > 20 lakhs
	high_loan_customers = merged[merged["LoanAmount"] > 2000000][
		["CustomerName", "LoanAmount", "LoanType", "LoanStatus"]
	]

	# Loans with pending payments
	pending_payments = merged[merged["PaymentStatus"] == "Pending"][
		["CustomerName", "LoanID", "EMIAmount", "AmountPaid", "PaymentStatus"]
	]

	# Fully paid loans
	fully_paid_loans = merged[merged["PaymentStatus"] == "Paid"][
		["CustomerName", "LoanID", "LoanAmount", "PaymentStatus"]
	]

	return {
		"top_10_loan_customers": top_10_loan_customers,
		"top_10_salary_customers": top_10_salary_customers,
		"low_credit_customers": low_credit_customers,
		"high_loan_customers": high_loan_customers,
		"pending_payments": pending_payments,
		"fully_paid_loans": fully_paid_loans,
	}


############################
# PART 7 - GROUPBY ANALYSIS
############################


def run_groupby_analysis(merged):
	# Group by City
	city_summary = merged.groupby("City").agg(
		NumberOfCustomers=("CustomerName", "count"),
		AverageSalary=("Salary", "mean"),
		TotalLoanAmount=("LoanAmount", "sum"),
	).reset_index()

	# Group by Loan Type
	loan_type_summary = merged.groupby("LoanType").agg(
		NumberOfLoans=("LoanID", "count"),
		AverageLoanAmount=("LoanAmount", "mean"),
		TotalLoanAmount=("LoanAmount", "sum"),
	).reset_index()

	# Group by Loan Status
	loan_status_summary = merged.groupby("LoanStatus").agg(
		Count=("LoanID", "count")
	).reset_index()

	# Group by PaymentStatus (count and total amount paid)
	payment_status_summary = merged.groupby("PaymentStatus").agg(
		Count=("LoanID", "count"),
		TotalAmountPaid=("AmountPaid", "sum"),
	).reset_index()

	return city_summary, loan_type_summary, loan_status_summary, payment_status_summary


############################
# PART 8 - BUSINESS RULE FLAGS
############################


def apply_business_rules(merged):
	# Create one flag column per rule
	merged["Flag_LoanAbove30L"] = merged["LoanAmount"] > 3000000
	merged["Flag_CreditBelow650"] = merged["CreditScore"] < 650
	merged["Flag_SalaryBelow30K"] = merged["Salary"] < 30000
	merged["Flag_DTIAbove5"] = merged["DebtToIncomeRatio"] > 5
	merged["Flag_EMIDueAbove10K"] = merged["EMIDue"] > 10000
	merged["Flag_PaymentPending"] = merged["PaymentStatus"] == "Pending"
	merged["Flag_LoanRejected"] = merged["LoanStatus"] == "Rejected"

	# Final combined risk flag
	merged["AnyRiskFlag"] = merged[
		[
			"Flag_LoanAbove30L",
			"Flag_CreditBelow650",
			"Flag_SalaryBelow30K",
			"Flag_DTIAbove5",
			"Flag_EMIDueAbove10K",
			"Flag_PaymentPending",
			"Flag_LoanRejected",
		]
	].any(axis=1)

	return merged


############################
# PART 9 - FINANCE METRICS
############################


def calculate_finance_metrics(merged):
	total_loan_portfolio = float(merged["LoanAmount"].sum())
	total_amount_collected = float(merged["AmountPaid"].sum())
	outstanding_amount = float((merged["LoanAmount"] - merged["AmountPaid"]).sum())

	loan_recovery_pct = (
		(total_amount_collected / total_loan_portfolio) * 100 if total_loan_portfolio else 0
	)
	default_pct = (
		(len(merged[merged["PaymentStatus"] == "Pending"]) / len(merged)) * 100 if len(merged) else 0
	)
	average_emi = float(merged["EMIAmount"].mean()) if len(merged) else 0
	average_credit_score = float(merged["CreditScore"].mean()) if len(merged) else 0

	return {
		"Total Loan Portfolio": round(total_loan_portfolio, 2),
		"Total Amount Collected": round(total_amount_collected, 2),
		"Outstanding Amount": round(outstanding_amount, 2),
		"Loan Recovery %": round(loan_recovery_pct, 2),
		"Default %": round(default_pct, 2),
		"Average EMI": round(average_emi, 2),
		"Average Credit Score": round(average_credit_score, 2),
	}


############################
# PART 10 - EXPORT REPORTS
############################


def export_reports(base_path, merged, city_summary, loan_type_summary, analysis_results, finance_metrics):
	# Export pending payments CSV
	analysis_results["pending_payments"].to_csv(base_path / "PendingPayments.csv", index=False)

	# Export loan summary workbook
	with pd.ExcelWriter(base_path / "LoanSummary.xlsx", engine="openpyxl") as writer:
		city_summary.to_excel(writer, sheet_name="CitySummary", index=False)
		loan_type_summary.to_excel(writer, sheet_name="LoanTypeSummary", index=False)
		pd.DataFrame(list(finance_metrics.items()), columns=["Metric", "Value"]).to_excel(
			writer, sheet_name="FinanceMetrics", index=False
		)

	# Export customer loan workbook
	with pd.ExcelWriter(base_path / "CustomerLoanReport.xlsx", engine="openpyxl") as writer:
		merged.to_excel(writer, sheet_name="AllCustomerLoans", index=False)
		analysis_results["top_10_loan_customers"].to_excel(writer, sheet_name="Top10LoanCustomers", index=False)
		analysis_results["low_credit_customers"].to_excel(writer, sheet_name="LowCreditCustomers", index=False)


############################
# MAIN FUNCTION - RUN ALL PARTS
############################


def main():
	base_path = Path(__file__).resolve().parent

	# Part 1
	customers, loan_applications, loan_payments = read_data(base_path)

	# Part 2
	customers, loan_applications, loan_payments = clean_data(
		customers, loan_applications, loan_payments
	)

	# Part 3
	merged, report_df = merge_datasets(customers, loan_applications, loan_payments)

	# Part 4
	merged = create_new_columns(merged)

	# Part 5
	numpy_metrics = get_numpy_metrics(merged)

	# Part 6
	analysis_results = run_pandas_analysis(merged)

	# Part 7
	city_summary, loan_type_summary, loan_status_summary, payment_status_summary = run_groupby_analysis(merged)

	# Part 8
	merged = apply_business_rules(merged)

	# Part 9
	finance_metrics = calculate_finance_metrics(merged)

	# Part 10
	export_reports(base_path, merged, city_summary, loan_type_summary, analysis_results, finance_metrics)

	# Expected outputs display section
	print("\n===== TOP 10 LOAN CUSTOMERS =====")
	print(analysis_results["top_10_loan_customers"].to_string(index=False))

	print("\n===== CUSTOMERS WITH LOW CREDIT SCORE (< 650) =====")
	print(analysis_results["low_credit_customers"].to_string(index=False))

	print("\n===== PENDING LOAN PAYMENTS =====")
	print(analysis_results["pending_payments"].to_string(index=False))

	print("\n===== CITY-WISE LOAN SUMMARY =====")
	print(city_summary.to_string(index=False))

	print("\n===== LOAN TYPE SUMMARY =====")
	print(loan_type_summary.to_string(index=False))

	print("\n===== LOAN RECOVERY REPORT =====")
	print(pd.DataFrame(list(finance_metrics.items()), columns=["Metric", "Value"]).to_string(index=False))

	print("\nGenerated files: LoanSummary.xlsx, CustomerLoanReport.xlsx, PendingPayments.csv")


if __name__ == "__main__":
	main()
