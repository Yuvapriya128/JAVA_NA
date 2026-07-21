export interface CreateLoanRequest {
  loanType: string;
  principalAmount: number;
  annualInterestRate: number;
  tenureMonths: number;
  customerId: number;
}

export interface LoanSummary {
  loanId: number;
  loanType: string;
  principalAmount: number;
  annualInterestRate: number;
  tenureMonths: number;
  emiAmount: number;
  totalInterest?: number;
  totalRepayment?: number;
  outstandingAmount?: number;
  loanStatus: string;
  customerId: number;
  customerName: string;
  city: string;
}

export interface LoanDashboard {
  totalCustomers: number;
  totalLoans: number;
  activeLoans: number;
  closedLoans: number;
  overdueEMIs: number;
  totalEMICollected: number;
  totalPenaltyCollected: number;
  averageInterestRate: number;
  highestOutstandingLoan: string;
  highestPayingCustomer: string;
  NPAAccounts: number;
}

export interface EmiPaymentRequest {
  amount: number;
  paymentMode: string;
  referenceNumber: string;
  emiId: number;
}

export interface EmiPaymentResponse {
  emiId: number;
  amountDue: number;
  amountPaid: number;
  penaltyAmount: number;
  status: string;
  daysPastDue: number;
  paymentDate: string;
}

export interface EmiPaymentHistory {
  emiId: number;
  loanId?: number;
  amountPaid: number;
  paymentMode: string;
  referenceNumber: string;
  status: string;
  penalty: number;
  daysPastDue: number;
  paymentDate: string;
}

export interface LoanProduct {
  code: string;
  displayName: string;
  defaultRate: number;
}

export interface CreateLoanApplicationRequest {
  loanType: string;
  customerId?: number;
  principalAmount?: number;
  tenureMonths?: number;
  annualInterestRate?: number;
}

export interface LoanApplicationDTO {
  applicationId: number;
  customerId: number;
  customerName: string;
  loanType: string;
  principalAmount: number;
  tenureMonths: number;
  annualInterestRate: number;
  applicationStatus: string;
  applicationDate: string;
  approvalDate?: string;
  rejectionReason?: string;
}

export interface UpdateLoanApplicationStatusRequest {
  status: string;
  rejectionReason?: string;
}

export interface EmiPaymentReceiptDTO {
  receiptNumber: string;
  generatedAt: string;
  emiId: number;
  installmentNumber: number;
  dueDate: string;
  amountDue: number;
  principalComponent: number;
  interestComponent: number;
  penaltyAmount: number;
  emiStatus: string;
  amountPaid: number;
  paymentDate?: string;
  paymentMode?: string;
  referenceNumber?: string;
  loanId: number;
  loanType: string;
  loanPrincipal: number;
  loanInterestRate: number;
  loanTenureMonths: number;
  disbursementDate: string;
  customerId: number;
  customerName: string;
  customerEmail: string;
  customerPhone: string;
  customerCity: string;
}

export interface EmiCalculationRequest {
  principalAmount: number;
  annualInterestRate: number;
  tenureMonths: number;
}

export interface EmiCalculationResponse {
  emiAmount: number;
  totalInterest: number;
  totalPayment: number;
}

export interface EmiSchedule {
  emiId: number;
  loanId: number;
  installmentNumber: number;
  dueDate: string;
  principalComponent: number;
  interestComponent: number;
  amountDue: number;
  amountPaid: number;
  status: string;
  penaltyAmount: number;
  daysPastDue: number;
}
