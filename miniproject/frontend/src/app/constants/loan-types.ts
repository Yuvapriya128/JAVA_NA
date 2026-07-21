export interface LoanTypeOption {
  code: string;
  label: string;
  icon?: string;
  defaultRate?: number;
}

export const LOAN_TYPE_OPTIONS: LoanTypeOption[] = [
  { code: 'HOME', label: 'Home Loan', icon: 'bi-house-door', defaultRate: 8.75 },
  { code: 'PERSONAL', label: 'Personal Loan', icon: 'bi-person', defaultRate: 12.99 },
  { code: 'VEHICLE', label: 'Vehicle Loan', icon: 'bi-truck', defaultRate: 10.1 },
  { code: 'AUTO', label: 'Auto Loan', icon: 'bi-car-front', defaultRate: 9.4 },
  { code: 'EDUCATION', label: 'Education Loan', icon: 'bi-mortarboard', defaultRate: 9.2 },
  { code: 'BUSINESS', label: 'Business Loan', icon: 'bi-briefcase', defaultRate: 11.5 },
  { code: 'GOLD', label: 'Gold Loan', icon: 'bi-gem', defaultRate: 10.6 },
  { code: 'SECURED', label: 'Secured Loan', icon: 'bi-shield-check', defaultRate: 9.8 },
  { code: 'UNSECURED', label: 'Unsecured Loan', icon: 'bi-shield-exclamation', defaultRate: 14.2 }
];

export function normalizeLoanType(value: string): string {
  const normalized = value.trim().toUpperCase();

  if (normalized === 'HOME_LOAN') {
    return 'HOME';
  }
  if (normalized === 'PERSONAL_LOAN') {
    return 'PERSONAL';
  }
  if (normalized === 'VEHICLE_LOAN') {
    return 'VEHICLE';
  }
  if (normalized === 'AUTO_LOAN' || normalized === 'AUTOMOBILE') {
    return 'AUTO';
  }

  return normalized;
}
