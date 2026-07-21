export type QuickActionRole = 'ADMIN' | 'MANAGER' | 'USER';
export type QuickActionGroup = 'Customers' | 'Loans' | 'EMI' | 'Reports' | 'Account' | 'System';

export interface QuickActionCommand {
  id: string;
  title: string;
  description: string;
  route: string;
  icon: string;
  group: QuickActionGroup;
  roles: QuickActionRole[];
  keywords: string[];
  shortcut?: string;
}

const ROUTES = {
  dashboard: '/dashboard',
  customers: '/customers',
  createCustomer: '/create-customer',
  loanProducts: '/loan-products',
  createLoan: '/create-loan',
  loans: '/loans',
  myRequests: '/my-requests',
  updateInterest: '/update-interest',
  interestManagement: '/interest-management',
  payEmi: '/emi/pay',
  paymentHistory: '/payments',
  reports: '/reports',
  profile: '/profile',
  settings: '/settings',
  contact: '/contact',
  about: '/about',
  logout: '/logout'
} as const;

export const QUICK_ACTION_COMMANDS: QuickActionCommand[] = [
  {
    id: 'dashboard',
    title: 'Dashboard',
    description: 'View system overview and operational metrics.',
    route: ROUTES.dashboard,
    icon: 'bi-speedometer2',
    group: 'System',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['home', 'overview', 'metrics', 'analytics'],
    shortcut: 'D'
  },
  {
    id: 'customers',
    title: 'Customers',
    description: 'Manage customer accounts and lifecycle.',
    route: ROUTES.customers,
    icon: 'bi-people',
    group: 'Customers',
    roles: ['ADMIN', 'MANAGER'],
    keywords: ['customer', 'people', 'accounts', 'crm']
  },
  {
    id: 'create-customer',
    title: 'Create Customer',
    description: 'Create a new customer profile.',
    route: ROUTES.createCustomer,
    icon: 'bi-person-plus',
    group: 'Customers',
    roles: ['ADMIN', 'MANAGER'],
    keywords: ['customer', 'new', 'add', 'onboard']
  },
  {
    id: 'loan-products',
    title: 'Loan Products',
    description: 'Browse products and initiate requests.',
    route: ROUTES.loanProducts,
    icon: 'bi-box',
    group: 'Loans',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['products', 'plans', 'catalog']
  },
  {
    id: 'create-loan',
    title: 'Create Loan',
    description: 'Create a new loan application.',
    route: ROUTES.createLoan,
    icon: 'bi-bank',
    group: 'Loans',
    roles: ['ADMIN', 'MANAGER'],
    keywords: ['loan', 'new loan', 'application', 'origination']
  },
  {
    id: 'loans',
    title: 'Loans',
    description: 'Review active loan portfolios.',
    route: ROUTES.loans,
    icon: 'bi-bank',
    group: 'Loans',
    roles: ['ADMIN', 'MANAGER'],
    keywords: ['loan list', 'portfolio', 'accounts']
  },
  {
    id: 'my-requests-user',
    title: 'My Requests',
    description: 'Track your submitted loan requests.',
    route: ROUTES.myRequests,
    icon: 'bi-list-check',
    group: 'Loans',
    roles: ['USER'],
    keywords: ['my loans', 'requests', 'applications', 'status']
  },
  {
    id: 'rate-control',
    title: 'Rate Control',
    description: 'Update benchmark rates for operations.',
    route: ROUTES.updateInterest,
    icon: 'bi-sliders',
    group: 'Loans',
    roles: ['ADMIN', 'MANAGER'],
    keywords: ['rate', 'interest', 'pricing', 'update interest']
  },
  {
    id: 'interest-management',
    title: 'Interest Management',
    description: 'Manage interest updates and governance.',
    route: ROUTES.interestManagement,
    icon: 'bi-activity',
    group: 'Loans',
    roles: ['ADMIN'],
    keywords: ['interest', 'rate control', 'governance']
  },
  {
    id: 'pay-emi',
    title: 'Pay EMI',
    description: 'Pay pending monthly installments.',
    route: ROUTES.payEmi,
    icon: 'bi-credit-card',
    group: 'EMI',
    roles: ['USER'],
    keywords: ['emi', 'payment', 'installment', 'due']
  },
  {
    id: 'payment-history',
    title: 'Payment History',
    description: 'Review payment transactions and receipts.',
    route: ROUTES.paymentHistory,
    icon: 'bi-clock-history',
    group: 'EMI',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['history', 'payments', 'receipts', 'ledger']
  },
  {
    id: 'reports',
    title: 'Reports',
    description: 'Open analytics and export reports.',
    route: ROUTES.reports,
    icon: 'bi-bar-chart',
    group: 'Reports',
    roles: ['ADMIN', 'MANAGER'],
    keywords: ['report', 'analytics', 'download', 'export']
  },
  {
    id: 'profile',
    title: 'Profile',
    description: 'View account profile and preferences.',
    route: ROUTES.profile,
    icon: 'bi-person-circle',
    group: 'Account',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['account', 'user profile', 'personal info']
  },
  {
    id: 'settings',
    title: 'Settings',
    description: 'Manage preferences and system settings.',
    route: ROUTES.settings,
    icon: 'bi-gear',
    group: 'System',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['preferences', 'configuration', 'setup']
  },
  {
    id: 'contact',
    title: 'Contact',
    description: 'Reach support and contact information.',
    route: ROUTES.contact,
    icon: 'bi-envelope',
    group: 'System',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['help', 'support', 'contact us']
  },
  {
    id: 'about',
    title: 'About',
    description: 'View company and platform information.',
    route: ROUTES.about,
    icon: 'bi-info-circle',
    group: 'System',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['company', 'about us', 'platform']
  },
  {
    id: 'logout',
    title: 'Logout',
    description: 'Securely sign out from this session.',
    route: ROUTES.logout,
    icon: 'bi-box-arrow-right',
    group: 'Account',
    roles: ['ADMIN', 'MANAGER', 'USER'],
    keywords: ['sign out', 'exit', 'logout']
  }
];
