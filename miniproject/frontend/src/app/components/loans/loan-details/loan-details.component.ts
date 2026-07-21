import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { RouterLink } from '@angular/router';
import { Router } from '@angular/router';
import { EmiPaymentHistory, EmiSchedule, LoanService, LoanSummary } from '../../../services/loan/loan.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { ConfirmationDialogService } from '../../../services/shared/confirmation-dialog.service';

@Component({
  selector: 'app-loan-details',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent, StatusBadgeComponent, RouterLink],
  templateUrl: './loan-details.component.html',
  styleUrls: ['./loan-details.component.css'],
})
export class LoanDetailsComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly confirmationDialog = inject(ConfirmationDialogService);

  readonly loan = signal<LoanSummary | null>(null);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');
  readonly activeTab = signal<'overview' | 'emi' | 'payments' | 'documents' | 'timeline' | 'support'>('overview');
  readonly emiSchedules = signal<EmiSchedule[]>([]);
  readonly paymentRows = signal<EmiPaymentHistory[]>([]);
  readonly timelineRemarks = signal('');

  ngOnInit(): void {
    const mode = this.route.snapshot.queryParamMap.get('mode');
    if (mode === 'edit') {
      this.status.set({
        loading: false,
        success: true,
        error: ''
      });
      this.successMessage.set('Edit mode opened. Use Update Interest Rate for now; full loan-field editing requires a backend update endpoint.');
    }

    this.loadData();
  }

  loadData(): void {
    const loanId = Number(this.route.snapshot.queryParamMap.get('id') || 1001);
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.getLoan(loanId).subscribe({
      next: (response) => {
        this.loan.set(response);
        this.loadLoanEmiSchedule(response.loanId);
        this.timelineRemarks.set(this.defaultTimelineRemark(response.loanStatus));
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
        this.successMessage.set('');
      }
    });
  }

  edit(): void {
    const currentLoan = this.loan();
    if (!currentLoan) {
      return;
    }

    this.router.navigate(['/update-interest'], {
      queryParams: {
        loanId: currentLoan.loanId,
        rate: currentLoan.annualInterestRate
      }
    });
  }

  async delete(): Promise<void> {
    const currentLoan = this.loan();
    if (!currentLoan) {
      return;
    }
    const confirmed = await this.confirmationDialog.confirm({
      title: 'Delete Loan',
      message: 'This action will permanently remove this loan record. Continue?',
      confirmText: 'Delete',
      cancelText: 'Cancel',
      variant: 'danger'
    });
    if (!confirmed) {
      return;
    }
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.deleteLoan(currentLoan.loanId).subscribe({
      next: () => {
        this.status.set({ loading: false, success: true, error: '' });
        this.successMessage.set('Loan deleted successfully.');
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
        this.successMessage.set('');
      }
    });
  }

  canManageLoan(): boolean {
    return this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  setActiveTab(tab: 'overview' | 'emi' | 'payments' | 'documents' | 'timeline' | 'support'): void {
    this.activeTab.set(tab);
  }

  paidPercent(): number {
    const total = this.emiSchedules().length;
    if (!total) {
      return 0;
    }
    const completed = this.emiSchedules().filter((emi) => this.isPaid(emi)).length;
    return Math.round((completed / total) * 100);
  }

  paidEmiCount(): number {
    return this.emiSchedules().filter((emi) => this.isPaid(emi)).length;
  }

  overdueEmiCount(): number {
    return this.emiSchedules().filter((emi) => this.isOverdue(emi)).length;
  }

  nextDueEmi(): EmiSchedule | null {
    const pending = this.emiSchedules()
      .filter((emi) => !this.isPaid(emi))
      .sort((left, right) => this.toDateValue(left.dueDate) - this.toDateValue(right.dueDate));
    return pending[0] || null;
  }

  totalInterestPaid(): number {
    const paidIds = new Set(this.paymentRows().map((row) => row.emiId));
    return this.emiSchedules()
      .filter((emi) => paidIds.has(emi.emiId))
      .reduce((sum, emi) => sum + Math.max(0, Number(emi.interestComponent) || 0), 0);
  }

  totalPrincipalPaid(): number {
    const paidIds = new Set(this.paymentRows().map((row) => row.emiId));
    return this.emiSchedules()
      .filter((emi) => paidIds.has(emi.emiId))
      .reduce((sum, emi) => sum + Math.max(0, Number(emi.principalComponent) || 0), 0);
  }

  totalPenaltyPaid(): number {
    return this.paymentRows().reduce((sum, row) => sum + Math.max(0, Number(row.penalty) || 0), 0);
  }

  totalAmountPaid(): number {
    return this.paymentRows().reduce((sum, row) => sum + Math.max(0, Number(row.amountPaid) || 0), 0);
  }

  remainingBalance(): number {
    const currentLoan = this.loan();
    if (!currentLoan) {
      return 0;
    }
    if (typeof currentLoan.outstandingAmount === 'number' && Number.isFinite(currentLoan.outstandingAmount)) {
      return Math.max(0, currentLoan.outstandingAmount);
    }

    const expected = this.emiSchedules().reduce((sum, emi) => {
      const installment = Math.max(0, Number(emi.principalComponent) || 0) + Math.max(0, Number(emi.interestComponent) || 0);
      return sum + installment;
    }, 0);
    return Math.max(0, expected - this.totalAmountPaid());
  }

  scheduleBadge(status: string): string {
    const normalized = String(status || '').trim().toUpperCase();
    if (normalized === 'PAID') {
      return 'Paid';
    }
    if (normalized === 'OVERDUE') {
      return 'Overdue';
    }
    return 'Pending';
  }

  loanHealthLabel(): string {
    const overdue = this.overdueEmiCount();
    if (overdue === 0) {
      return 'Healthy';
    }
    if (overdue <= 2) {
      return 'Watch';
    }
    return 'Critical';
  }

  formatCurrency(value: number): string {
    return `INR ${value.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
  }

  formatOptionalCurrency(value?: number): string {
    if (typeof value !== 'number' || !Number.isFinite(value)) {
      return 'N/A';
    }
    return this.formatCurrency(value);
  }

  formatDate(value: string): string {
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

  private loadLoanEmiSchedule(loanId: number): void {
    const service = this.loanService as unknown as { getEmisByLoan?: (id: number, page?: number, size?: number) => { subscribe: Function } };
    if (typeof service.getEmisByLoan !== 'function') {
      this.emiSchedules.set([]);
      this.loadLoanPayments(loanId);
      return;
    }
    service.getEmisByLoan(loanId, 0, 300).subscribe({
      next: (response: { content: EmiSchedule[] }) => {
        this.emiSchedules.set((response as { content: EmiSchedule[] }).content || []);
        this.loadLoanPayments(loanId);
      },
      error: () => {
        this.emiSchedules.set([]);
        this.loadLoanPayments(loanId);
      }
    });
  }

  private loadLoanPayments(loanId: number): void {
    const service = this.loanService as unknown as { getEmiPayments?: (page?: number, size?: number) => { subscribe: Function } };
    if (typeof service.getEmiPayments !== 'function') {
      this.paymentRows.set([]);
      return;
    }
    service.getEmiPayments(0, 500).subscribe({
      next: (response: EmiPaymentHistory[] | { content: EmiPaymentHistory[] }) => {
        const normalized = response as EmiPaymentHistory[] | { content: EmiPaymentHistory[] };
        const rows = Array.isArray(normalized) ? normalized : normalized.content;
        const scheduleIds = new Set(this.emiSchedules().map((emi) => emi.emiId));
        const filtered = rows.filter((row) => {
          if (typeof row.loanId === 'number') {
            return row.loanId === loanId;
          }
          return scheduleIds.has(row.emiId);
        });
        this.paymentRows.set(filtered);
      },
      error: () => {
        this.paymentRows.set([]);
      }
    });
  }

  private isPaid(emi: EmiSchedule): boolean {
    const status = String(emi.status || '').trim().toUpperCase();
    return status === 'PAID' || (Number(emi.amountDue) || 0) <= 0;
  }

  private isOverdue(emi: EmiSchedule): boolean {
    const status = String(emi.status || '').trim().toUpperCase();
    if (status === 'OVERDUE') {
      return true;
    }
    const due = this.toDateValue(emi.dueDate);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return !this.isPaid(emi) && due < today.getTime();
  }

  private toDateValue(value: string): number {
    const parsed = new Date(value).getTime();
    if (Number.isNaN(parsed)) {
      return Number.MAX_SAFE_INTEGER;
    }
    return parsed;
  }

  private defaultTimelineRemark(status: string): string {
    const normalized = String(status || '').trim().toUpperCase();
    if (normalized === 'CLOSED') {
      return 'Loan fully serviced and marked as closed.';
    }
    if (normalized === 'ACTIVE') {
      return 'Loan created and active with EMI repayment in progress.';
    }
    if (normalized === 'DISBURSED') {
      return 'Funds disbursed and repayment schedule started.';
    }
    return 'Loan lifecycle is currently being tracked.';
  }
}
