import { Component, EventEmitter, HostListener, Input, Output, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { DashboardService } from '../../../services/dashboard/dashboard.service';
import { LoanService, LoanApplicationDTO } from '../../../services/loan/loan.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { ThemeService } from '../../../services/shared/theme.service';
import { QuickActionsComponent } from '../../shared/quick-actions/quick-actions.component';
import { ConfirmationDialogService } from '../../../services/shared/confirmation-dialog.service';

interface NotificationItem {
  title: string;
  message: string;
  icon: string;
  time: string;
  route: string;
  unread: boolean;
  category: 'Loan Due' | 'Overdue' | 'Payment' | 'Interest Update' | 'Customer Activity';
}

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, QuickActionsComponent],
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.css']})
export class NavbarComponent implements OnInit {
  @Input() isSidebarOpen = true;
  @Output() toggleSidebar = new EventEmitter<void>();

  showNotifications = false;
  showProfileMenu = false;
  showQuickActions = false;
  notifications: NotificationItem[] = [];

  private readonly router = inject(Router);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly authService = inject(AuthService);
  private readonly loanService = inject(LoanService);
  private readonly dashboardService = inject(DashboardService);
  private readonly themeService = inject(ThemeService);
  private readonly confirmationDialog = inject(ConfirmationDialogService);

  ngOnInit(): void {
    this.themeService.initializeTheme();
    this.loadNotifications();
  }

  currentTheme(): 'light' | 'dark' {
    return this.themeService.theme();
  }

  toggleTheme(): void {
    this.themeService.toggleTheme();
  }

  getUserRole(): string {
    return this.getRole();
  }

  markAllAsRead(): void {
    this.notifications = this.notifications.map((item) => ({ ...item, unread: false }));
  }

  openNotification(item: NotificationItem): void {
    this.notifications = this.notifications.map((entry) =>
      entry.title === item.title ? { ...entry, unread: false } : entry
    );
    this.router.navigate([item.route]);
    this.closeMenus();
  }

  toggleNotifications(): void {
    this.showNotifications = !this.showNotifications;
    if (this.showNotifications) {
      this.showProfileMenu = false;
    }
  }

  toggleProfileMenu(): void {
    this.showProfileMenu = !this.showProfileMenu;
    if (this.showProfileMenu) {
      this.showNotifications = false;
    }
  }

  closeMenus(): void {
    this.showNotifications = false;
    this.showProfileMenu = false;
  }

  openQuickActions(): void {
    this.closeMenus();
    this.showQuickActions = true;
  }

  closeQuickActions(): void {
    this.showQuickActions = false;
  }

  async confirmLogout(): Promise<void> {
    const confirmed = await this.confirmationDialog.confirm({
      title: 'Sign out',
      message: 'Are you sure you want to logout?',
      confirmText: 'Logout',
      cancelText: 'Stay signed in',
      variant: 'warning'
    });
    if (!confirmed) {
      return;
    }
    this.closeMenus();
    this.router.navigate(['/logout']);
  }

  notificationItems(): NotificationItem[] {
    return this.notifications;
  }

  groupedNotifications(): { category: string; items: NotificationItem[] }[] {
    const groups = new Map<string, NotificationItem[]>();
    this.notifications.forEach((item) => {
      const existing = groups.get(item.category) || [];
      groups.set(item.category, [...existing, item]);
    });

    return Array.from(groups.entries()).map(([category, items]) => ({ category, items }));
  }

  notificationCount(): number {
    return this.notifications.filter((item) => item.unread).length;
  }

  @HostListener('document:keydown', ['$event'])
  handleShortcuts(event: KeyboardEvent): void {
    if ((event.ctrlKey || event.metaKey) && event.key.toLowerCase() === 'k') {
      event.preventDefault();
      this.openQuickActions();
      return;
    }

    if (event.key === 'Escape' && this.showQuickActions) {
      event.preventDefault();
      this.closeQuickActions();
    }
  }

  @HostListener('document:click', ['$event'])
  handleDocumentClick(event: MouseEvent): void {
    const target = event.target as HTMLElement | null;
    if (!target) {
      return;
    }

    if (target.closest('.right-cluster')) {
      return;
    }

    this.closeMenus();
  }

  private getRole(): 'USER' | 'MANAGER' | 'ADMIN' {
    const role = String(this.tokenStorage.getPrimaryRole() || '').replace('ROLE_', '');
    if (role === 'ADMIN' || role === 'MANAGER') {
      return role;
    }
    return 'USER';
  }

  private loadNotifications(): void {
    const role = this.getRole();
    if (role === 'USER') {
      this.loadUserNotifications();
      return;
    }

    this.loadElevatedNotifications(role);
  }

  private loadUserNotifications(): void {
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.loanService.getMyLoanApplications(0, 100).subscribe({
          next: (applicationsResponse) => {
            this.loanService.getEmiPayments(0, 100).subscribe({
              next: (paymentResponse) => {
                const applications = applicationsResponse.content;
                const payments = Array.isArray(paymentResponse) ? paymentResponse : paymentResponse.content;
                const scopedPayments = payments.filter((row) => {
                  const entry = row as unknown as { customerId?: number; customerName?: string; customerEmail?: string };
                  if (typeof entry.customerId === 'number') {
                    return entry.customerId === user.customerId;
                  }
                  if (entry.customerEmail) {
                    return entry.customerEmail.toLowerCase() === user.email.toLowerCase();
                  }
                  if (entry.customerName) {
                    return entry.customerName.toLowerCase() === user.customerName.toLowerCase();
                  }
                  return false;
                });

                const pending = applications.filter((item) => this.normalizeApplicationStatus(item) === 'PENDING').length;
                const underReview = applications.filter((item) => this.normalizeApplicationStatus(item) === 'UNDER_REVIEW').length;
                const approved = applications.filter((item) => this.normalizeApplicationStatus(item) === 'APPROVED').length;

                const notifications: NotificationItem[] = [
                  {
                    title: 'My Requests',
                    message: `Pending ${pending}, Under Review ${underReview}, Approved ${approved}.`,
                    icon: 'bi-hourglass-split',
                    time: 'Now',
                    route: '/my-requests',
                    category: 'Customer Activity',
                    unread: true
                  }
                ];

                if (scopedPayments.length > 0) {
                  const latestPayment = [...scopedPayments].sort((a, b) => {
                    const left = new Date(a.paymentDate).getTime();
                    const right = new Date(b.paymentDate).getTime();
                    return right - left;
                  })[0];

                  notifications.push({
                    title: 'Latest Payment Recorded',
                    message: `EMI #${latestPayment.emiId} was paid via ${latestPayment.paymentMode}.`,
                    icon: 'bi-receipt',
                    time: 'Now',
                    route: '/payments',
                    category: 'Payment',
                    unread: true
                  });
                } else {
                  notifications.push({
                    title: 'No Payment History Yet',
                    message: 'You can start paying EMIs once a loan is active for your account.',
                    icon: 'bi-credit-card-2-front',
                    time: 'Now',
                    route: '/emi/pay',
                    category: 'Loan Due',
                    unread: true
                  });
                }

                this.notifications = notifications;
              },
              error: () => {
                this.notifications = [
                  {
                    title: 'Notifications Unavailable',
                    message: 'Unable to load your payment notifications right now.',
                    icon: 'bi-bell-slash',
                    time: 'Now',
                    route: '/payments',
                    category: 'Payment',
                    unread: true
                  }
                ];
              }
            });
          },
          error: () => {
            this.notifications = [
              {
                title: 'Notifications Unavailable',
                message: 'Unable to load your request notifications right now.',
                icon: 'bi-bell-slash',
                time: 'Now',
                route: '/my-requests',
                category: 'Customer Activity',
                unread: true
              }
            ];
          }
        });
      },
      error: () => {
        this.notifications = [
          {
            title: 'Profile Unavailable',
            message: 'Unable to resolve current user profile for notifications.',
            icon: 'bi-exclamation-circle',
            time: 'Now',
            route: '/profile',
            category: 'Customer Activity',
            unread: true
          }
        ];
      }
    });
  }

  private loadElevatedNotifications(role: 'MANAGER' | 'ADMIN'): void {
    const metricsRequest = role === 'ADMIN'
      ? this.dashboardService.getAdminDashboard()
      : this.dashboardService.getDashboard();

    metricsRequest.subscribe({
      next: (dashboard) => {
        const overdue = Number.isFinite(dashboard.overdueEMIs) ? dashboard.overdueEMIs : 0;
        const activeLoans = Number.isFinite(dashboard.activeLoans) ? dashboard.activeLoans : 0;
        const npa = Number.isFinite(dashboard.NPAAccounts) ? dashboard.NPAAccounts : 0;

        const notifications: NotificationItem[] = [
          {
            title: role === 'ADMIN' ? 'Enterprise Risk Snapshot' : 'Portfolio Snapshot',
            message: `Active loans ${activeLoans}, overdue EMIs ${overdue}.`,
            icon: 'bi-graph-up-arrow',
            time: 'Now',
            route: '/dashboard',
            category: 'Overdue',
            unread: true
          },
          {
            title: role === 'ADMIN' ? 'Risk Accounts' : 'Collections Focus',
            message: role === 'ADMIN'
              ? `NPA accounts currently at ${npa}.`
              : `Review overdue EMIs to maintain portfolio quality.`,
            icon: role === 'ADMIN' ? 'bi-shield-exclamation' : 'bi-hourglass-split',
            time: 'Now',
            route: role === 'ADMIN' ? '/dashboard' : '/loans',
            category: 'Customer Activity',
            unread: true
          },
          {
            title: 'Reports Ready',
            message: 'Open reports for detailed trend analysis and exports.',
            icon: 'bi-bar-chart',
            time: 'Now',
            route: '/reports',
            category: 'Interest Update',
            unread: true
          }
        ];

        this.notifications = notifications;
      },
      error: () => {
        this.notifications = [
          {
            title: 'Notifications Unavailable',
            message: 'Unable to load dashboard-driven notifications right now.',
            icon: 'bi-bell-slash',
            time: 'Now',
            route: '/dashboard',
            category: 'Customer Activity',
            unread: true
          }
        ];
      }
    });
  }

  private normalizeApplicationStatus(item: LoanApplicationDTO): 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' {
    const normalized = String(item.applicationStatus || '').trim().toUpperCase();
    if (normalized === 'APPROVED') {
      return 'APPROVED';
    }
    if (normalized === 'REJECTED') {
      return 'REJECTED';
    }
    if (normalized === 'UNDER_REVIEW' || normalized === 'IN_REVIEW') {
      return 'UNDER_REVIEW';
    }
    return 'PENDING';
  }
}
