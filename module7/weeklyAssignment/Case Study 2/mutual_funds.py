import pandas as pd
import numpy as np

####################
#Part - 1: Load the data from CSV files
###################

funds = pd.read_csv('funds.csv')
investors = pd.read_csv('investors.csv')
transactions = pd.read_csv('transactions.csv')
nav_history = pd.read_csv('nav_history.csv')

#######################
#Part - 2: Data Cleaning and Preprocessing
#######################

#remove duplicate rows in each dataframe
funds.drop_duplicates(inplace=True)
investors.drop_duplicates(inplace=True) 
transactions.drop_duplicates(inplace=True)
nav_history.drop_duplicates(inplace=True)

#check for missing values in each dataframe
print("Missing values in funds dataframe:\n", funds.isnull().sum())
print("Missing values in investors dataframe:\n", investors.isnull().sum())
print("Missing values in transactions dataframe:\n", transactions.isnull().sum())
print("Missing values in nav_history dataframe:\n", nav_history.isnull().sum())

#fill missing values in nav_history dataframe with forward fill method
nav_history = nav_history.ffill()

#replace missing InvestorType with "Retail"
investors['InvestorType'] = investors['InvestorType'].fillna('Retail').astype(str).str.strip()

#remove rows having negative values in nav_history dataframe
nav_history = nav_history[nav_history['NAV'] >= 0]

#convert date columns to datetime format
if 'InceptionDate' in funds.columns:
	funds['InceptionDate'] = pd.to_datetime(funds['InceptionDate'], errors='coerce')

if 'DateOfBirth' in investors.columns:
	investors['DateOfBirth'] = pd.to_datetime(investors['DateOfBirth'], errors='coerce')

if 'TransactionDate' in transactions.columns:
	transactions['TransactionDate'] = pd.to_datetime(transactions['TransactionDate'], errors='coerce')

if 'PurchaseDate' in transactions.columns:
	transactions['PurchaseDate'] = pd.to_datetime(transactions['PurchaseDate'], errors='coerce')

nav_history['Date'] = pd.to_datetime(nav_history['Date'], errors='coerce')


############################
#PART -3 -MERGE DATAFRAMES
###########################

'''
merge all dataframes into a single dataframe for analysis
Required columns
Investor Name
Fund Name
Category
AMC
State
Units Purchased
Purchase NAV
Latest NAV
'''

# get latest NAV available for each fund
latest_nav = (
	nav_history.sort_values('Date')
	.groupby('FundID', as_index=False)
	.last()[['FundID', 'NAV']]
	.rename(columns={'NAV': 'Latest NAV'})
)

# merge all four datasets
merged_df = (
	transactions
	.merge(investors[['InvestorID', 'InvestorName', 'State']], on='InvestorID', how='left')
	.merge(funds[['FundID', 'FundName', 'Category', 'AMC']], on='FundID', how='left')
	.merge(latest_nav, on='FundID', how='left')
)

# rename and keep only required columns
merged_df = merged_df.rename(columns={
	'InvestorName': 'Investor Name',
	'FundName': 'Fund Name',
	'UnitsPurchased': 'Units Purchased',
	'PurchaseNAV': 'Purchase NAV'
})

merged_df = merged_df[
	[
		'Investor Name',
		'Fund Name',
		'Category',
		'AMC',
		'State',
		'Units Purchased',
		'Purchase NAV',
		'Latest NAV'
	]
]

############
#PART 4- CREATE NEW COLUMNS
############

'''
Calculate
Investment Amount
Investment Amount = Units Purchased × Purchase NAV
'''

merged_df['Investment Amount'] = merged_df['Units Purchased'] * merged_df['Purchase NAV']

'''
Current Value
Current Value = Units Purchased × Latest NAV
'''

merged_df['Current Value'] = merged_df['Units Purchased'] * merged_df['Latest NAV']

'''
Profit
Profit = Current Value − Investment Amount
'''

merged_df['Profit'] = merged_df['Current Value'] - merged_df['Investment Amount']


'''
ROI %=((Current Value- Investment Amount)/Investment Amount )×100
'''

merged_df['ROI %'] = ((merged_df['Current Value'] - merged_df['Investment Amount']) / merged_df['Investment Amount']) * 100


###########
#PART 5--NUMPY TASKS
###########


nav_values = nav_history['NAV'].to_numpy()

average_nav = np.mean(nav_values)
maximum_nav = np.max(nav_values)
minimum_nav = np.min(nav_values)
variance_nav = np.var(nav_values)
std_dev_nav = np.std(nav_values)

# Rolling average with window size 5 using NumPy
rolling_average_nav = np.convolve(nav_values, np.ones(5) / 5, mode='valid')

print("Average NAV:", average_nav)
print("Maximum NAV:", maximum_nav)
print("Minimum NAV:", minimum_nav)
print("Variance of NAV:", variance_nav)
print("Standard Deviation of NAV:", std_dev_nav)
print("Rolling Average (window=5):", rolling_average_nav)

###############
#PART 6 - PANDAS ANALYSIS
###############


# Top 5 investors based on total investment amount
top_5_investors = (
	merged_df.groupby('Investor Name', as_index=False)['Investment Amount']
	.sum()
	.sort_values('Investment Amount', ascending=False)
	.head(5)
)

# Top 5 profitable funds based on total profit
top_5_profitable_funds = (
	merged_df.groupby('Fund Name', as_index=False)['Profit']
	.sum()
	.sort_values('Profit', ascending=False)
	.head(5)
)

# Worst performing fund based on total profit
worst_performing_fund = (
	merged_df.groupby('Fund Name', as_index=False)['Profit']
	.sum()
	.sort_values('Profit', ascending=True)
	.head(1)
)

# Highest and lowest NAV funds based on Latest NAV
fund_nav = merged_df[['Fund Name', 'Latest NAV']].drop_duplicates(subset=['Fund Name'])
highest_nav_fund = fund_nav.sort_values('Latest NAV', ascending=False).head(1)
lowest_nav_fund = fund_nav.sort_values('Latest NAV', ascending=True).head(1)

print("Top 5 investors based on investment amount:")
print(top_5_investors)

print("Top 5 profitable funds:")
print(top_5_profitable_funds)

print("Worst performing fund:")
print(worst_performing_fund)

print("Highest NAV fund:")
print(highest_nav_fund)

print("Lowest NAV fund:")
print(lowest_nav_fund)

################
#PART 7 - GROUPBY
################

'''
Part 7 Description:
Use GroupBy operations to summarize investment and return metrics by
Category, AMC, State, and Investor Type.
'''

def part7_groupby_analysis(merged_data, nav_data, investors_data):
	'''
	Function Description:
	Compute all required GroupBy summaries and return them as DataFrames.
	'''
	category_summary = merged_data.groupby('Category', as_index=False).agg(
		Average_ROI=('ROI %', 'mean'),
		Average_NAV=('Latest NAV', 'mean'),
		Total_Investment=('Investment Amount', 'sum')
	)

	amc_summary = merged_data.groupby('AMC', as_index=False).agg(
		NumberofFunds=('Fund Name', 'nunique'),
		Average_NAV=('Latest NAV', 'mean'),
		Total_Investment=('Investment Amount', 'sum')
	)

	state_summary = merged_data.groupby('State', as_index=False).agg(
		NumberofInvestors=('Investor Name', 'nunique'),
		Total_Investment=('Investment Amount', 'sum'),
		Average_ROI=('ROI %', 'mean')
	)

	investor_type_map = investors_data[['InvestorName', 'InvestorType']].drop_duplicates('InvestorName')
	merged_with_type = merged_data.merge(
		investor_type_map,
		left_on='Investor Name',
		right_on='InvestorName',
		how='left'
	)

	investor_type_summary = merged_with_type.groupby('InvestorType', as_index=False).agg(
		Total_Investment=('Investment Amount', 'sum'),
		Average_Profit=('Profit', 'mean')
	)

	return category_summary, amc_summary, state_summary, investor_type_summary


category_summary, amc_summary, state_summary, investor_type_summary = part7_groupby_analysis(
	merged_df, nav_history, investors
)

print("Category GroupBy Summary:")
print(category_summary)

print("AMC GroupBy Summary:")
print(amc_summary)

print("State GroupBy Summary:")
print(state_summary)

print("Investor Type GroupBy Summary:")
print(investor_type_summary)


######################
#PART 8 - DETECT ISSUES
######################

'''
Part 8 Description:
Detect data quality issues in NAV and transaction data.
Checks include duplicates, negative values, future dates, missing IDs,
and invalid purchase NAV values.
'''

def part8_detect_issues(nav_data, txn_data, fund_data, investor_data):
	'''
	Function Description:
	Return a dictionary of issue DataFrames for all required checks.
	'''
	nav_copy = nav_data.copy()
	txn_copy = txn_data.copy()

	nav_copy['Date'] = pd.to_datetime(nav_copy['Date'], errors='coerce')
	txn_copy['PurchaseDate'] = pd.to_datetime(txn_copy['PurchaseDate'], errors='coerce')

	today = pd.Timestamp.today().normalize()

	duplicate_nav_records = nav_copy[nav_copy.duplicated(subset=['FundID', 'Date', 'NAV'], keep=False)]
	negative_nav = nav_copy[nav_copy['NAV'] < 0]
	future_dates = pd.concat([
		nav_copy[nav_copy['Date'] > today][['FundID', 'Date', 'NAV']],
		txn_copy[txn_copy['PurchaseDate'] > today][['TransactionID', 'PurchaseDate']]
	], ignore_index=True)

	missing_fund_ids = txn_copy[
		txn_copy['FundID'].isna() | (~txn_copy['FundID'].isin(fund_data['FundID']))
	]
	missing_investor_ids = txn_copy[
		txn_copy['InvestorID'].isna() | (~txn_copy['InvestorID'].isin(investor_data['InvestorID']))
	]
	invalid_purchase_nav = txn_copy[txn_copy['PurchaseNAV'] < 0]

	return {
		'Duplicate NAV records': duplicate_nav_records,
		'Negative NAV': negative_nav,
		'Future dates': future_dates,
		'Missing Fund IDs': missing_fund_ids,
		'Missing Investor IDs': missing_investor_ids,
		'Invalid Purchase NAV (<0)': invalid_purchase_nav
	}


issue_results = part8_detect_issues(nav_history, transactions, funds, investors)

for issue_name, issue_df in issue_results.items():
	print(issue_name + ":")
	print(issue_df)


#########################
#PART 9 - FINANCE METRICS
#########################

'''
Part 9 Description:
Calculate ROI, Absolute Return, Annual Return, Volatility, and Sharpe Ratio.
Assumptions:
- Holding Period = 1 year
- Risk Free Rate = 6%
'''

def part9_finance_metrics(merged_data, nav_data):
	'''
	Function Description:
	Add finance metric columns to merged_data and return key scalar metrics.
	'''
	finance_df = merged_data.copy()

	# ROI = ((Current Value - Investment Amount) / Investment Amount) * 100
	finance_df['ROI %'] = ((finance_df['Current Value'] - finance_df['Investment Amount']) / finance_df['Investment Amount']) * 100

	# Absolute Return = Current Value - Investment Amount
	finance_df['Absolute Return'] = finance_df['Current Value'] - finance_df['Investment Amount']

	# Annual Return with holding period = 1 year
	finance_df['Annual Return %'] = (((finance_df['Current Value'] / finance_df['Investment Amount']) ** (1 / 1)) - 1) * 100

	volatility = np.std(nav_data['NAV'].to_numpy())
	average_return = finance_df['Annual Return %'].mean()
	risk_free_rate = 6
	sharpe_ratio = (average_return - risk_free_rate) / volatility if volatility != 0 else np.nan

	return finance_df, volatility, sharpe_ratio


merged_df, volatility, sharpe_ratio = part9_finance_metrics(merged_df, nav_history)

print("Volatility:", volatility)
print("Sharpe Ratio:", sharpe_ratio)


########################
#PART 10 - EXPORT REPORTS
########################

'''
Part 10 Description:
Generate export reports for top funds, investor summaries,
and category summaries.

Expected Outputs:
- Top Performing Funds (Highest ROI, Highest Profit, Highest NAV)
- Worst Performing Fund (Lowest ROI)
- State-wise Investment
- AMC-wise Investment
- Category-wise ROI
'''

def part10_export_reports(merged_data, category_grp, amc_grp, state_grp):
	'''
	Function Description:
	Prepare report DataFrames and export to Excel/CSV files.
	'''
	top_roi_fund = merged_data.sort_values('ROI %', ascending=False).head(1)
	top_profit_fund = merged_data.sort_values('Profit', ascending=False).head(1)
	top_nav_fund = merged_data.sort_values('Latest NAV', ascending=False).head(1)
	worst_roi_fund = merged_data.sort_values('ROI %', ascending=True).head(1)

	top_performing_funds = pd.concat([
		top_roi_fund.assign(Highlight='Highest ROI'),
		top_profit_fund.assign(Highlight='Highest Profit'),
		top_nav_fund.assign(Highlight='Highest NAV')
	], ignore_index=True)

	worst_performing_fund = worst_roi_fund.assign(Highlight='Lowest ROI')

	investor_summary = merged_data.groupby('Investor Name', as_index=False).agg(
		Total_Investment=('Investment Amount', 'sum'),
		Total_Profit=('Profit', 'sum'),
		Average_ROI=('ROI %', 'mean')
	)

	category_summary_export = category_grp.rename(columns={'Average_ROI': 'Category-wise ROI'})

	with pd.ExcelWriter('TopFunds.xlsx') as writer:
		top_performing_funds.to_excel(writer, sheet_name='Top Performing Funds', index=False)
		worst_performing_fund.to_excel(writer, sheet_name='Worst Performing Fund', index=False)

	with pd.ExcelWriter('InvestorSummary.xlsx') as writer:
		investor_summary.to_excel(writer, sheet_name='Investor Summary', index=False)
		state_grp.to_excel(writer, sheet_name='State-wise Investment', index=False)
		amc_grp.to_excel(writer, sheet_name='AMC-wise Investment', index=False)

	category_summary_export.to_csv('CategorySummary.csv', index=False)

	return top_performing_funds, worst_performing_fund, investor_summary, category_summary_export


top_performing_funds_report, worst_performing_fund_report, investor_summary_report, category_summary_report = part10_export_reports(
	merged_df, category_summary, amc_summary, state_summary
)

print("Top Performing Funds Report:")
print(top_performing_funds_report)

print("Worst Performing Fund Report:")
print(worst_performing_fund_report)

print("State-wise Investment:")
print(state_summary)

print("AMC-wise Investment:")
print(amc_summary)

print("Category-wise ROI:")
print(category_summary_report)





