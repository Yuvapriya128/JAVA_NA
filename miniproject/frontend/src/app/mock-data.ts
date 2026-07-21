export interface DashboardMetric {
  label: string;
  value: string;
  icon: string;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'info';
}

export interface CustomerRow {
  customerId: number;
  customerName: string;
  email: string;
  phoneNumber: string;
  city: string;
  creditScore: number;
  role: string;
  active: boolean;
}

export interface LoanRow {
  loanId: number;
  loanType: string;
  principalAmount: string;
  annualInterestRate: string;
  tenureMonths: number;
  emiAmount: string;
  loanStatus: string;
  customerName: string;
  city: string;
}

export interface LoanProduct {
  code: string;
  name: string;
  rate: string;
  description: string;
  eligibility: string;
}

export interface EmiPaymentRow {
  emiId: number;
  amountPaid: string;
  paymentMode: string;
  referenceNumber: string;
  status: string;
  penalty: string;
  daysPastDue: number;
  paymentDate: string;
}

export const dashboardMetrics: DashboardMetric[] = [
  { label: 'Total Customers', value: '2,480', icon: 'bi-people-fill', tone: 'primary' },
  { label: 'Total Loans', value: '4,915', icon: 'bi-bank2', tone: 'info' },
  { label: 'Active Loans', value: '4,021', icon: 'bi-graph-up-arrow', tone: 'success' },
  { label: 'Closed Loans', value: '894', icon: 'bi-check2-circle', tone: 'primary' },
  { label: 'Overdue EMIs', value: '132', icon: 'bi-exclamation-triangle-fill', tone: 'warning' },
  { label: 'NPA Accounts', value: '28', icon: 'bi-shield-exclamation', tone: 'danger' }
];

export const collectionStats = [
  { title: 'Total EMI Collected', value: 'INR 8.42 Cr', growth: '+8.4% vs last month' },
  { title: 'Penalty Collected', value: 'INR 32.5 L', growth: '+3.1% vs last month' },
  { title: 'Average Interest Rate', value: '10.85%', growth: '-0.2% optimized' }
];

export const highlights = [
  'Highest Outstanding Loan: Rajesh Menon - INR 42.8 L',
  'Highest Paying Customer: Meera Iyer - INR 11.2 L paid this quarter',
  'Expected collections this week: INR 2.1 Cr',
  'Top delinquency cluster: Chennai Metro South'
];

export const recentActivities = [
  'Loan #1008 approved for Kavya R. (Home Loan)',
  'Customer #204 deactivated by admin governance team',
  'Bulk interest revision pushed for Vehicle Loan portfolio',
  'EMI receipt generated for EMI #8012 via UPI'
];

export const customers: CustomerRow[] = [
  {
    customerId: 101,
    customerName: 'Aarav Sharma',
    email: 'aarav.sharma@bankmail.in',
    phoneNumber: '9876543210',
    city: 'Mumbai',
    creditScore: 812,
    role: 'USER',
    active: true
  },
  {
    customerId: 102,
    customerName: 'Nisha Verma',
    email: 'nisha.verma@bankmail.in',
    phoneNumber: '9988776655',
    city: 'Pune',
    creditScore: 742,
    role: 'MANAGER',
    active: true
  },
  {
    customerId: 103,
    customerName: 'Rohan Iyer',
    email: 'rohan.iyer@bankmail.in',
    phoneNumber: '9123456780',
    city: 'Bengaluru',
    creditScore: 621,
    role: 'USER',
    active: false
  }
];

export const loans: LoanRow[] = [
  {
    loanId: 1001,
    loanType: 'HOME',
    principalAmount: 'INR 45,00,000',
    annualInterestRate: '8.75%',
    tenureMonths: 240,
    emiAmount: 'INR 39,720',
    loanStatus: 'ACTIVE',
    customerName: 'Aarav Sharma',
    city: 'Mumbai'
  },
  {
    loanId: 1002,
    loanType: 'PERSONAL',
    principalAmount: 'INR 8,00,000',
    annualInterestRate: '13.40%',
    tenureMonths: 60,
    emiAmount: 'INR 18,395',
    loanStatus: 'OVERDUE',
    customerName: 'Nisha Verma',
    city: 'Pune'
  },
  {
    loanId: 1003,
    loanType: 'VEHICLE',
    principalAmount: 'INR 12,50,000',
    annualInterestRate: '10.10%',
    tenureMonths: 84,
    emiAmount: 'INR 21,009',
    loanStatus: 'CLOSED',
    customerName: 'Rohan Iyer',
    city: 'Bengaluru'
  }
];

export const loanProducts: LoanProduct[] = [
  {
    code: 'HOME',
    name: 'Home Loan',
    rate: '8.75% onward',
    description: 'Financing for apartment, villa, and plot purchase with flexible tenure.',
    eligibility: 'Salaried or self-employed with credit score 700+'
  },
  {
    code: 'PERSONAL',
    name: 'Personal Loan',
    rate: '12.99% onward',
    description: 'Quick unsecured loan for medical, education, and lifestyle needs.',
    eligibility: 'Stable income profile and clean repayment history'
  },
  {
    code: 'VEHICLE',
    name: 'Vehicle Loan',
    rate: '9.40% onward',
    description: 'New and used vehicle financing with fast disbursal turnaround.',
    eligibility: 'Minimum annual income INR 3.6 L and KYC compliant'
  },
  {
    code: 'AUTO',
    name: 'Auto Loan',
    rate: '9.10% onward',
    description: 'Passenger car financing with quick approval and transparent EMI plans.',
    eligibility: 'Salaried or self-employed with stable income and valid KYC'
  },
  {
    code: 'EDUCATION',
    name: 'Education Loan',
    rate: '9.25% onward',
    description: 'Study financing for domestic and international programs including living expenses.',
    eligibility: 'Confirmed admission with co-applicant and required documentation'
  },
  {
    code: 'BUSINESS',
    name: 'Business Loan',
    rate: '11.50% onward',
    description: 'Working capital and growth funding for MSMEs and established businesses.',
    eligibility: 'Business vintage 2+ years with audited financials'
  },
  {
    code: 'GOLD',
    name: 'Gold Loan',
    rate: '10.60% onward',
    description: 'Instant liquidity against pledged gold with flexible repayment options.',
    eligibility: 'Verified gold collateral and standard KYC compliance'
  },
  {
    code: 'SECURED',
    name: 'Secured Loan',
    rate: '9.80% onward',
    description: 'Collateral-backed loan products for larger sanctioned limits.',
    eligibility: 'Acceptable collateral valuation and repayment capacity'
  },
  {
    code: 'UNSECURED',
    name: 'Unsecured Loan',
    rate: '14.20% onward',
    description: 'Fast unsecured credit for short-term personal or business requirements.',
    eligibility: 'Strong credit profile and stable repayment history'
  }
];

export const emiPayments: EmiPaymentRow[] = [
  {
    emiId: 8012,
    amountPaid: 'INR 39,720',
    paymentMode: 'UPI',
    referenceNumber: 'UPI-TRX-445901',
    status: 'PAID',
    penalty: 'INR 0',
    daysPastDue: 0,
    paymentDate: '11 Jul 2026'
  },
  {
    emiId: 8013,
    amountPaid: 'INR 10,000',
    paymentMode: 'NET_BANKING',
    referenceNumber: 'NB-998712',
    status: 'PARTIAL',
    penalty: 'INR 420',
    daysPastDue: 5,
    paymentDate: '09 Jul 2026'
  },
  {
    emiId: 8014,
    amountPaid: 'INR 0',
    paymentMode: 'CARD',
    referenceNumber: 'NA',
    status: 'OVERDUE',
    penalty: 'INR 1,350',
    daysPastDue: 18,
    paymentDate: 'Pending'
  }
];
