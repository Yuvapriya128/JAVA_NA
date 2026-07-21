import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { AuthService } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { DashboardService } from '../../../services/dashboard/dashboard.service';
import { LoanService } from '../../../services/loan/loan.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

type NotificationCategory = 'Loan Due' | 'Overdue' | 'Payment' | 'Application' | 'Risk';

interface NotificationRow {
  title: string;
  message: string;
  category: NotificationCategory;
  time: string;
  unread: boolean;
}

@Component({
  selector: 'app-notification-center',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent],
  templateUrl: './notification-center.component.html',
  styleUrls: ['./notification-center.component.css']
})
export class NotificationCenterComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly dashboardService = inject(DashboardService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly authService = inject(AuthService);
  private currentUserCustomerId: number | null = null;

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly rows = signal<NotificationRow[]>([]);
  readonly filter = signal<'ALL' | NotificationCategory>('ALL');

  ngOnInit(): void {
    if (this.isUser()) {
      this.loadUserNotifications();
      return;
    }
    this.loadElevatedNotifications();
  }

  filteredRows(): NotificationRow[] {
    const selected = this.filter();
    if (selected === 'ALL') {
      return this.rows();
    }
    return this.rows().filter((row) => row.category === selected);
  }

  markAllRead(): void {
    this.rows.set(this.rows().map((row) => ({ ...row, unread: false })));
  }

  unreadCount(): number {
    return this.rows().filter((row) => row.unread).length;
  }

  setFilter(value: 'ALL' | NotificationCategory): void {
    this.filter.set(value);
  }

  private loadUserNotifications(): void {
    const cached = this.authService.getCurrentUserSync();
    if (cached?.customerId) {
      this.currentUserCustomerId = cached.customerId;
      this.fetchUserNotificationData();
      return;
    }
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserCustomerId = user.customerId;
        this.fetchUserNotificationData();
      },
      error: () => {
        this.rows.set([]);
      }
    });
  }

  private fetchUserNotificationData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.loanService.getMyLoanApplications(0, 100).subscribe({
      next: (applicationsResponse) => {
        this.loanService.getEmiPayments(0, 200).subscribe({
          next: (paymentsResponse) => {
            const applications = applicationsResponse.content;
            const allPayments = Array.isArray(paymentsResponse) ? paymentsResponse : paymentsResponse.content;
            const payments = allPayments.filter((row) => {
              const details = row as unknown as { customerId?: number };
              return this.currentUserCustomerId !== null && details.customerId === this.currentUserCustomerId;
            });
            const pendingApps = applications.filter((item) => this.normalizeApplicationStatus(item.applicationStatus) === 'PENDING').length;
            const reviewApps = applications.filter((item) => this.normalizeApplicationStatus(item.applicationStatus) === 'UNDER_REVIEW').length;
            const latestPayment = payments.length > 0
              ? [...payments].sort((left, right) => new Date(right.paymentDate).getTime() - new Date(left.paymentDate).getTime())[0]
              : null;
            const rows: NotificationRow[] = [
              {
                title: 'Application Update',
                message: `Pending applications: ${pendingApps}, under review: ${reviewApps}.`,
                category: 'Application',
                time: 'Now',
                unread: true
              }
            ];

            if (latestPayment) {
              rows.push({
                title: 'Payment Recorded',
                message: `EMI #${latestPayment.emiId} paid via ${latestPayment.paymentMode}.`,
                category: 'Payment',
                time: latestPayment.paymentDate,
                unread: true
              });
            }
            this.rows.set(rows);
            this.status.set({ loading: false, success: false, error: '' });
          },
          error: () => {
            this.status.set({ loading: false, success: false, error: 'Unable to load payment notifications.' });
          }
        });
      },
      error: () => {
        this.status.set({ loading: false, success: false, error: 'Unable to load application notifications.' });
      }
    });
  }

  private loadElevatedNotifications(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.dashboardService.getDashboard().subscribe({
      next: (dashboard) => {
        this.rows.set([
          {
            title: 'Overdue Alert',
            message: `Current overdue EMIs: ${dashboard.overdueEMIs}`,
            category: 'Overdue',
            time: 'Now',
            unread: true
          },
          {
            title: 'Risk Watch',
            message: `NPA accounts currently at ${dashboard.NPAAccounts}.`,
            category: 'Risk',
            time: 'Now',
            unread: true
          },
          {
            title: 'Collection Pulse',
            message: `Total EMI collected: INR ${Number(dashboard.totalEMICollected || 0).toLocaleString('en-IN')}.`,
            category: 'Payment',
            time: 'Now',
            unread: true
          }
        ]);
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to load notification center.'
        });
      }
    });
  }

  private isUser(): boolean {
    return !this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  private normalizeApplicationStatus(status: string): 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' {
    const normalized = String(status || '').trim().toUpperCase();
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

