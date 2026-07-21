import { Routes } from '@angular/router';
import { AuthLayoutComponent } from './components/layout/auth-layout/auth-layout.component';
import { DashboardLayoutComponent } from './components/layout/dashboard-layout/dashboard-layout.component';
import { MainLayoutComponent } from './components/layout/main-layout/main-layout.component';
import { authGuard } from './core/guards/auth.guard';
import { loginRedirectGuard } from './core/guards/login-redirect.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
	{ path: '', pathMatch: 'full', loadComponent: () => import('./components/public/landing-page/landing-page.component').then((m) => m.LandingPageComponent), data: { title: 'LoanHub' } },
	{ path: 'about', loadComponent: () => import('./components/public/about/about.component').then((m) => m.AboutComponent), data: { title: 'About Us' } },
	{ path: 'contact', loadComponent: () => import('./components/public/contact/contact.component').then((m) => m.ContactComponent), data: { title: 'Contact Us' } },
	{ path: 'privacy', loadComponent: () => import('./components/public/privacy/privacy.component').then((m) => m.PrivacyComponent), data: { title: 'Privacy Policy' } },
	{ path: 'terms', loadComponent: () => import('./components/public/terms/terms.component').then((m) => m.TermsComponent), data: { title: 'Terms of Service' } },
	{
		path: 'login',
		component: AuthLayoutComponent,
		canActivate: [loginRedirectGuard],
		children: [{ path: '', loadComponent: () => import('./components/auth/login/login.component').then((m) => m.LoginComponent), data: { title: 'Login' } }]
	},
	{
		path: 'register',
		component: AuthLayoutComponent,
		canActivate: [loginRedirectGuard],
		children: [{ path: '', loadComponent: () => import('./components/auth/register/register.component').then((m) => m.RegisterComponent), data: { title: 'Register' } }]
	},
	{ path: 'access-denied', loadComponent: () => import('./components/errors/access-denied/access-denied.component').then((m) => m.AccessDeniedComponent) },
	{ path: 'logout', loadComponent: () => import('./components/auth/logout/logout.component').then((m) => m.LogoutComponent), canActivate: [authGuard] },
	{
		path: '',
		component: MainLayoutComponent,
		canActivate: [authGuard],
		children: [
			{ path: '', redirectTo: 'dashboard', pathMatch: 'full' },
			{
				path: 'dashboard',
				component: DashboardLayoutComponent,
				children: [
					{ path: '', loadComponent: () => import('./components/dashboard/dashboard.component').then((m) => m.DashboardComponent), data: { title: 'Dashboard' } }
				]
			},
			{ path: 'admin-dashboard', redirectTo: 'dashboard', pathMatch: 'full' },
			{ path: 'customers', loadComponent: () => import('./components/customers/customer-list/customer-list.component').then((m) => m.CustomerListComponent), canActivate: [roleGuard], data: { title: 'Customers', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'create-customer', loadComponent: () => import('./components/customers/customer-form/customer-form.component').then((m) => m.CustomerFormComponent), canActivate: [roleGuard], data: { title: 'Add Customer', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'customers/new', loadComponent: () => import('./components/customers/customer-form/customer-form.component').then((m) => m.CustomerFormComponent), canActivate: [roleGuard], data: { title: 'Add Customer', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'customers/details', loadComponent: () => import('./components/customers/customer-details/customer-details.component').then((m) => m.CustomerDetailsComponent), data: { title: 'Customer Details' } },
			{ path: 'loans', loadComponent: () => import('./components/loans/loan-list/loan-list.component').then((m) => m.LoanListComponent), data: { title: 'Loans' } },
			{ path: 'loans/foreclosure', loadComponent: () => import('./components/loans/foreclosure/foreclosure.component').then((m) => m.ForeclosureComponent), data: { title: 'Foreclosure' } },
			{ path: 'create-loan', loadComponent: () => import('./components/loans/create-loan/create-loan.component').then((m) => m.CreateLoanComponent), canActivate: [roleGuard], data: { title: 'Create Loan', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'loans/new', loadComponent: () => import('./components/loans/create-loan/create-loan.component').then((m) => m.CreateLoanComponent), canActivate: [roleGuard], data: { title: 'Create Loan', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'loans/details', loadComponent: () => import('./components/loans/loan-details/loan-details.component').then((m) => m.LoanDetailsComponent), data: { title: 'Loan Details' } },
			{ path: 'loan-products', loadComponent: () => import('./components/loans/loan-products/loan-products.component').then((m) => m.LoanProductsComponent), data: { title: 'Loan Products' } },
			{ path: 'applications', loadComponent: () => import('./components/applications/application-center/application-center.component').then((m) => m.ApplicationCenterComponent), data: { title: 'Applications' } },
			{ path: 'my-requests', loadComponent: () => import('./components/loans/loan-products/loan-products.component').then((m) => m.LoanProductsComponent), canActivate: [roleGuard], data: { title: 'My Requests', roles: ['USER'] } },
			{ path: 'update-interest', loadComponent: () => import('./components/loans/interest-management/interest-management.component').then((m) => m.InterestManagementComponent), canActivate: [roleGuard], data: { title: 'Interest Management', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'interest-management', loadComponent: () => import('./components/loans/interest-management/interest-management.component').then((m) => m.InterestManagementComponent), canActivate: [roleGuard], data: { title: 'Interest Management', roles: ['ADMIN'] } },
			{ path: 'emi', loadComponent: () => import('./components/emi/pay-emi/pay-emi.component').then((m) => m.PayEmiComponent), canActivate: [roleGuard], data: { title: 'Pay EMI', roles: ['USER'] } },
			{ path: 'emi/pay', loadComponent: () => import('./components/emi/pay-emi/pay-emi.component').then((m) => m.PayEmiComponent), canActivate: [roleGuard], data: { title: 'Pay EMI', roles: ['USER'] } },
			{ path: 'payments', loadComponent: () => import('./components/emi/payment-history/payment-history.component').then((m) => m.PaymentHistoryComponent), data: { title: 'Payment History' } },
			{ path: 'emi/history', loadComponent: () => import('./components/emi/payment-history/payment-history.component').then((m) => m.PaymentHistoryComponent), data: { title: 'Payment History' } },
			{ path: 'profile', loadComponent: () => import('./components/profile/profile.component').then((m) => m.ProfileComponent), data: { title: 'Profile' } },
			{ path: 'reports', loadComponent: () => import('./components/reports/reports-center/reports-center.component').then((m) => m.ReportsCenterComponent), canActivate: [roleGuard], data: { title: 'Reports', subtitle: 'Analytics and exports', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'notifications', loadComponent: () => import('./components/notifications/notification-center/notification-center.component').then((m) => m.NotificationCenterComponent), data: { title: 'Notifications' } },
			{ path: 'support', loadComponent: () => import('./components/support/support-center/support-center.component').then((m) => m.SupportCenterComponent), data: { title: 'Support', subtitle: 'Ticketing and customer helpdesk' } },
			{ path: 'administration', loadComponent: () => import('./components/administration/admin-center/admin-center.component').then((m) => m.AdminCenterComponent), canActivate: [roleGuard], data: { title: 'Administration', subtitle: 'Governance and platform settings', roles: ['MANAGER', 'ADMIN'] } },
			{ path: 'settings', loadComponent: () => import('./components/settings/settings-center/settings-center.component').then((m) => m.SettingsCenterComponent), data: { title: 'Settings', subtitle: 'Configuration and preferences' } }
		]
	},
	{ path: '**', loadComponent: () => import('./components/errors/not-found/not-found.component').then((m) => m.NotFoundComponent) }
];
