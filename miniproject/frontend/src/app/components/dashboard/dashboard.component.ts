import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { TokenStorageService } from '../../services/auth/token-storage.service';
import { AuthService } from '../../services/auth/auth.service';
import { PageHeaderComponent } from '../shared/page-header/page-header.component';
import { StatCardComponent } from '../shared/cards/stat-card.component';
import { SummaryCardComponent } from './summary-card/summary-card.component';
import { QuickActionCardComponent } from './quick-action-card/quick-action-card.component';
import { ActivityCardComponent } from './activity-card/activity-card.component';
import { NotificationCardComponent } from './notification-card/notification-card.component';
import { DashboardService, EmiInsights } from '../../services/dashboard/dashboard.service';
import { EmiSchedule, LoanService, LoanSummary } from '../../services/loan/loan.service';
import { UiStatusState, defaultUiStatus } from '../../constants/ui-status';
import { catchError, forkJoin, map, of } from 'rxjs';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    PageHeaderComponent,
    StatCardComponent,
    SummaryCardComponent,
    QuickActionCardComponent,
    ActivityCardComponent,
    NotificationCardComponent
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']})
export class DashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly loanService = inject(LoanService);
  private readonly authService = inject(AuthService);
  private currentUserCustomerId: number | null = null;

  readonly metrics = signal<Array<{ label: string; value: string; icon: string; tone: 'primary' | 'info' | 'success' | 'warning' | 'danger' }>>([]);
  readonly collections = signal<Array<{ title: string; value: string; growth: string }>>([]);
  readonly keyHighlights = signal<string[]>([]);
  readonly activities = signal<string[]>([]);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly emiInsights = signal<EmiInsights | null>(null);
  readonly emiInsightsStatus = signal({ loading: false, error: '' });
  readonly role = signal<'USER' | 'MANAGER' | 'ADMIN'>(this.resolveRole());
  readonly collectionTrend = signal([62, 68, 65, 74, 78, 71, 82]);
  readonly overdueBreakup = signal([
    { label: 'On-Time', value: 72 },
    { label: 'Due Soon', value: 18 },
    { label: 'Overdue', value: 10 }
  ]);
  readonly upcomingEmis = signal<Array<{ loanId: number; customer: string; dueDate: string; amount: string }>>([]);
  readonly topDefaulters = signal<Array<{ customer: string; overdueCount: number; overdueAmount: string }>>([]);

  readonly quickActionsByRole = {
    USER: [
      { label: 'Pay EMI', icon: 'bi-credit-card', className: 'btn btn-primary text-start', routerLink: '/emi/pay' },
      { label: 'Track Application', icon: 'bi-hourglass-split', className: 'btn btn-outline-warning text-start', routerLink: '/applications' },
      { label: 'View Payment History', icon: 'bi-receipt', className: 'btn btn-outline-primary text-start', routerLink: '/payments' },
      { label: 'Apply Loan', icon: 'bi-collection', className: 'btn btn-outline-secondary text-start', routerLink: '/applications' },
      { label: 'Support', icon: 'bi-life-preserver', className: 'btn btn-outline-info text-start', routerLink: '/support' }
    ],
    MANAGER: [
      { label: 'Add Customer', icon: 'bi-person-plus', className: 'btn btn-primary text-start', routerLink: '/create-customer' },
      { label: 'Create Loan', icon: 'bi-cash-coin', className: 'btn btn-outline-primary text-start', routerLink: '/create-loan' },
      { label: 'Review Customers', icon: 'bi-people', className: 'btn btn-outline-secondary text-start', routerLink: '/customers' },
      { label: 'Update Interest', icon: 'bi-percent', className: 'btn btn-outline-warning text-start', routerLink: '/update-interest' }
    ],
    ADMIN: [
      { label: 'Manage Customers', icon: 'bi-people', className: 'btn btn-primary text-start', routerLink: '/customers' },
      { label: 'Create Loan', icon: 'bi-cash-coin', className: 'btn btn-outline-secondary text-start', routerLink: '/create-loan' },
      { label: 'Govern Interest Rules', icon: 'bi-percent', className: 'btn btn-outline-warning text-start', routerLink: '/update-interest' }
    ]
  };

  ngOnInit(): void {
    if (this.role() === 'USER') {
      this.loadUserData();
      return;
    }

    this.loadEmiInsights();
    this.loadData();
  }

  loadData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.dashboardService.getDashboard().subscribe({
      next: (response) => {
        const totalCustomers = this.toNumber(response.totalCustomers);
        const totalLoans = this.toNumber(response.totalLoans);
        const activeLoans = this.toNumber(response.activeLoans);
        const closedLoans = this.toNumber(response.closedLoans);
        const overdueEmis = this.toNumber(response.overdueEMIs);
        const npaAccounts = this.toNumber(response.NPAAccounts);
        const totalEmiCollected = this.toNumber(response.totalEMICollected);
        const totalPenaltyCollected = this.toNumber(response.totalPenaltyCollected);
        const averageInterestRate = this.toNumber(response.averageInterestRate);

        const baseMetrics: Array<{ label: string; value: string; icon: string; tone: 'primary' | 'info' | 'success' | 'warning' | 'danger' }> = [
          { label: 'Total Customers', value: this.formatCount(totalCustomers), icon: 'bi-people-fill', tone: 'primary' },
          { label: 'Total Loans', value: this.formatCount(totalLoans), icon: 'bi-bank2', tone: 'info' },
          { label: 'Active Loans', value: this.formatCount(activeLoans), icon: 'bi-graph-up-arrow', tone: 'success' },
          { label: 'Closed Loans', value: this.formatCount(closedLoans), icon: 'bi-check2-circle', tone: 'primary' },
          { label: 'Overdue EMIs', value: this.formatCount(overdueEmis), icon: 'bi-exclamation-triangle-fill', tone: 'warning' }
        ];

        if (this.role() === 'ADMIN') {
          baseMetrics.push({ label: 'NPA Accounts', value: this.formatCount(npaAccounts), icon: 'bi-shield-exclamation', tone: 'danger' });
        }

        this.metrics.set(baseMetrics);
        this.collections.set([
          { title: 'Total EMI Collected', value: this.formatCurrency(totalEmiCollected), growth: 'Live from API' },
          { title: 'Penalty Collected', value: this.formatCurrency(totalPenaltyCollected), growth: 'Live from API' },
          { title: 'Average Interest Rate', value: this.formatPercent(averageInterestRate), growth: 'Live from API' }
        ]);
        const highlightsByRole = [
          `Highest Outstanding Loan: ${this.formatText(response.highestOutstandingLoan)}`,
          `Highest Paying Customer: ${this.formatText(response.highestPayingCustomer)}`,
          `Overdue EMIs: ${this.formatCount(overdueEmis)}`
        ];

        if (this.role() === 'ADMIN') {
          highlightsByRole.push(`NPA Accounts: ${this.formatCount(npaAccounts)}`);
        }

        this.keyHighlights.set(highlightsByRole);
        this.activities.set([
          `Dashboard synced with latest portfolio totals (${this.formatCount(totalLoans)} loans).`,
          `Collections refreshed: ${this.formatCurrency(totalEmiCollected)} total EMI collected.`,
          `Overdue trend currently at ${this.formatCount(overdueEmis)} records.`
        ]);
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
      }
    });
  }

  quickActions() {
    return this.quickActionsByRole[this.role()];
  }

  titleForRole(): string {
    if (this.role() === 'ADMIN') {
      return 'Enterprise Control Dashboard';
    }
    if (this.role() === 'MANAGER') {
      return 'Branch Operations Dashboard';
    }
    return 'Customer Self-Service Dashboard';
  }

  subtitleForRole(): string {
    if (this.role() === 'ADMIN') {
      return 'Risk and Governance Overview';
    }
    if (this.role() === 'MANAGER') {
      return 'Portfolio Monitoring and Team Actions';
    }
    return 'Payments, Products, and Personal Loan View';
  }

  quickActionTitle(): string {
    return this.role() === 'USER' ? 'My Quick Actions' : 'Operational Quick Actions';
  }

  canViewEmiInsights(): boolean {
    return this.role() !== 'USER';
  }

  retryEmiInsights(): void {
    if (!this.canViewEmiInsights()) {
      return;
    }
    this.loadEmiInsights();
  }

  private loadUserData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.metrics.set([
      { label: 'Total Outstanding', value: 'INR 0', icon: 'bi-currency-rupee', tone: 'warning' },
      { label: 'Next EMI Due', value: 'N/A', icon: 'bi-calendar-check', tone: 'info' },
      { label: 'Active Loans', value: '0', icon: 'bi-bank2', tone: 'success' },
      { label: 'Pending Applications', value: '0', icon: 'bi-hourglass-top', tone: 'primary' }
    ]);
    this.collections.set([
      { title: 'Current Outstanding', value: 'INR 0', growth: 'Across all active loans' },
      { title: 'Last EMI Paid', value: 'N/A', growth: 'Recent payment will appear here' },
      { title: 'My Request Status', value: 'Loading...', growth: 'Fetching latest request updates' }
    ]);
    this.keyHighlights.set([
      'Submit a product request to start your loan journey.',
      'Track all review decisions in My Requests.',
      'Payment and loan insights appear after loan activation.'
    ]);
    this.activities.set([
      'Welcome to your self-service dashboard.',
      'Use Applications to submit your first request.',
      'Payment and reminders update after loan activation.'
    ]);
    this.upcomingEmis.set([]);
    this.topDefaulters.set([]);
    this.resolveUserContext();
    this.loadUserRequestSummary();
  }

  private loadUserRequestSummary(): void {
    this.loanService.getMyLoanApplications(0, 100).subscribe({
      next: (response) => {
        const rows = response.content;
        const pending = rows.filter((item) => this.normalizeApplicationStatus(item.applicationStatus) === 'PENDING').length;
        const underReview = rows.filter((item) => this.normalizeApplicationStatus(item.applicationStatus) === 'UNDER_REVIEW').length;
        const approved = rows.filter((item) => this.normalizeApplicationStatus(item.applicationStatus) === 'APPROVED').length;
        const rejected = rows.filter((item) => this.normalizeApplicationStatus(item.applicationStatus) === 'REJECTED').length;

        this.metrics.set([
          this.metrics()[0],
          this.metrics()[1],
          this.metrics()[2],
          { label: 'Pending Applications', value: String(pending + underReview), icon: 'bi-hourglass-top', tone: 'primary' }
        ]);

        this.collections.set([
          this.collections()[0],
          this.collections()[1],
          {
            title: 'My Request Status',
            value: `Pending ${pending} | Review ${underReview}`,
            growth: `Approved ${approved} | Rejected ${rejected}`
          }
        ]);

        this.keyHighlights.set([
          ...this.keyHighlights().slice(0, 2),
          `My requests: Pending ${pending}, Under Review ${underReview}, Approved ${approved}, Rejected ${rejected}`
        ]);
      },
      error: () => {
        const summaryCards = [...this.collections()];
        summaryCards[2] = {
          title: 'My Request Status',
          value: 'Temporarily unavailable',
          growth: 'Open My Requests for detailed tracking'
        };
        this.collections.set(summaryCards);
      }
    });
  }

  private resolveUserContext(): void {
    const cached = this.authService.getCurrentUserSync();
    if (cached?.customerId) {
      this.currentUserCustomerId = cached.customerId;
      this.loadUserLoanAndPaymentSummary();
      return;
    }

    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserCustomerId = user.customerId;
        this.loadUserLoanAndPaymentSummary();
      },
      error: () => {
        this.status.set({ loading: false, success: false, error: '' });
      }
    });
  }

  private loadUserLoanAndPaymentSummary(): void {
    if (this.currentUserCustomerId === null) {
      this.status.set({ loading: false, success: false, error: '' });
      return;
    }

    this.loanService.getLoans(0, 300).subscribe({
      next: (loanResponse) => {
        const userLoans = loanResponse.content.filter((loan) => loan.customerId === this.currentUserCustomerId);
        const openLoans = userLoans.filter((loan) => !this.isLoanClosed(loan));
        const activeLoans = userLoans.filter((loan) => String(loan.loanStatus || '').toUpperCase() === 'ACTIVE');
        const outstanding = this.calculateLoanOutstanding(openLoans);

        const existingMetrics = this.metrics();
        this.metrics.set([
          { ...existingMetrics[0], value: this.formatCurrency(outstanding) },
          existingMetrics[1],
          { ...existingMetrics[2], value: String(activeLoans.length) },
          existingMetrics[3]
        ]);

        const summaryCards = [...this.collections()];
        summaryCards[0] = { ...summaryCards[0], value: this.formatCurrency(outstanding) };
        this.collections.set(summaryCards);

        this.upcomingEmis.set([]);
        this.loadUserEmiSnapshot(openLoans, outstanding);
        this.loadUserLatestPayment();
      },
      error: () => {
        this.status.set({ loading: false, success: false, error: '' });
      }
    });
  }

  private loadUserEmiSnapshot(loans: LoanSummary[], loanOutstandingFallback: number): void {
    if (loans.length === 0) {
      this.status.set({ loading: false, success: false, error: '' });
      return;
    }

    const requests = loans.map((loan) =>
      this.loanService.getEmisByLoan(loan.loanId, 0, 300).pipe(
        map((response) => ({ loan, emis: response.content })),
        catchError(() => of({ loan, emis: [] as EmiSchedule[] }))
      )
    );

    forkJoin(requests).subscribe({
      next: (results) => {
        const allPendingEmis = results
          .flatMap((item) => item.emis)
          .filter((emi) => !this.isEmiPaid(emi));

        const nextEmi = [...allPendingEmis]
          .filter((emi) => this.getOutstandingForEmi(emi) > 0)
          .sort((left, right) => this.toDateValue(left.dueDate) - this.toDateValue(right.dueDate))[0];

        const currentOutstanding = allPendingEmis
          .filter((emi) => this.toDateValue(emi.dueDate) <= this.todayStartValue())
          .reduce((sum, emi) => sum + this.getOutstandingForEmi(emi), 0);

        const scheduleOutstanding = allPendingEmis
          .reduce((sum, emi) => sum + this.getOutstandingForEmi(emi), 0);

        const totalOutstanding = scheduleOutstanding > 0 ? scheduleOutstanding : loanOutstandingFallback;
        const normalizedCurrentOutstanding = currentOutstanding > 0 ? currentOutstanding : totalOutstanding;

        const existingMetrics = this.metrics();
        this.metrics.set([
          { ...existingMetrics[0], value: this.formatCurrency(totalOutstanding) },
          {
            ...existingMetrics[1],
            value: nextEmi
              ? `${this.formatCurrency(this.getOutstandingForEmi(nextEmi))} (${this.formatDate(nextEmi.dueDate)})`
              : 'N/A'
          },
          existingMetrics[2],
          existingMetrics[3]
        ]);

        const summaryCards = [...this.collections()];
        summaryCards[0] = {
          ...summaryCards[0],
          value: this.formatCurrency(normalizedCurrentOutstanding),
          growth: currentOutstanding > 0 ? 'Due as of today (including overdue)' : 'Across all active loans'
        };
        this.collections.set(summaryCards);

        const upcoming = results
          .map((item) => {
            const nextByLoan = item.emis
              .filter((emi) => !this.isEmiPaid(emi) && this.getOutstandingForEmi(emi) > 0)
              .sort((left, right) => this.toDateValue(left.dueDate) - this.toDateValue(right.dueDate))[0];
            if (!nextByLoan) {
              return null;
            }
            return {
              loanId: item.loan.loanId,
              customer: item.loan.customerName,
              dueDate: this.formatDate(nextByLoan.dueDate),
              amount: this.formatCurrency(this.getOutstandingForEmi(nextByLoan))
            };
          })
          .filter((entry): entry is { loanId: number; customer: string; dueDate: string; amount: string } => entry !== null)
          .sort((left, right) => this.toDateValue(left.dueDate) - this.toDateValue(right.dueDate))
          .slice(0, 4);

        this.upcomingEmis.set(upcoming);
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: () => {
        this.status.set({ loading: false, success: false, error: '' });
      }
    });
  }

  private loadUserLatestPayment(): void {
    this.loanService.getEmiPayments(0, 200).subscribe({
      next: (paymentResponse) => {
        const rows = Array.isArray(paymentResponse) ? paymentResponse : paymentResponse.content;
        const scoped = rows.filter((row) => Number((row as { customerId?: number }).customerId) === this.currentUserCustomerId);
        if (scoped.length === 0) {
          return;
        }
        const latest = [...scoped].sort((left, right) => new Date(right.paymentDate).getTime() - new Date(left.paymentDate).getTime())[0];
        const cards = [...this.collections()];
        cards[1] = {
          title: 'Last EMI Paid',
          value: this.formatCurrency(Number(latest.amountPaid) || 0),
          growth: `${this.formatDate(latest.paymentDate)} via ${latest.paymentMode}`
        };
        this.collections.set(cards);
      }
    });
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

  maxTrendValue(): number {
    return Math.max(...this.collectionTrend(), 1);
  }

  private isLoanClosed(loan: LoanSummary): boolean {
    return String(loan.loanStatus || '').toUpperCase() === 'CLOSED';
  }

  private calculateLoanOutstanding(loans: LoanSummary[]): number {
    return loans.reduce((sum, loan) => sum + Math.max(0, Number(loan.outstandingAmount) || 0), 0);
  }

  private isEmiPaid(emi: EmiSchedule): boolean {
    return String(emi.status || '').toUpperCase() === 'PAID' || this.getOutstandingForEmi(emi) <= 0;
  }

  private getOutstandingForEmi(emi: EmiSchedule): number {
    const amountDue = Math.max(0, Number(emi.amountDue) || 0);
    if (amountDue > 0) {
      return amountDue;
    }
    const pending =
      Math.max(0, Number(emi.principalComponent) || 0) +
      Math.max(0, Number(emi.interestComponent) || 0) +
      Math.max(0, Number(emi.penaltyAmount) || 0) -
      Math.max(0, Number(emi.amountPaid) || 0);
    return Math.max(0, pending);
  }

  private toDateValue(value: string): number {
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return Number.MAX_SAFE_INTEGER;
    }
    return parsed.getTime();
  }

  private todayStartValue(): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.getTime();
  }

  private loadEmiInsights(): void {
    this.emiInsightsStatus.set({ loading: true, error: '' });
    this.dashboardService.getEmiInsights().subscribe({
      next: (response) => {
        this.emiInsights.set(response);
        this.emiInsightsStatus.set({ loading: false, error: '' });
      },
      error: () => {
        this.emiInsights.set(null);
        this.emiInsightsStatus.set({
          loading: false,
          error: 'EMI insights are temporarily unavailable. Please try again in a moment.'
        });
      }
    });
  }

  private resolveRole(): 'USER' | 'MANAGER' | 'ADMIN' {
    const role = this.tokenStorage.getPrimaryRole();
    if (role === 'ADMIN' || role === 'MANAGER') {
      return role;
    }
    return 'USER';
  }

  private toNumber(value: unknown): number | null {
    if (typeof value === 'number' && Number.isFinite(value)) {
      return value;
    }
    return null;
  }

  private formatCount(value: number | null): string {
    return value === null ? 'N/A' : String(value);
  }

  private formatCurrency(value: number | null): string {
    return value === null ? 'N/A' : `INR ${value.toLocaleString('en-IN')}`;
  }

  private formatDate(value: string): string {
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value || 'N/A';
    }
    return parsed.toLocaleDateString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric'
    });
  }

  private formatPercent(value: number | null): string {
    return value === null ? 'N/A' : `${value}%`;
  }

  private formatText(value: unknown): string {
    if (typeof value === 'string' && value.trim().length > 0) {
      return value;
    }
    return 'N/A';
  }
}
