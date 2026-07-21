import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { EmiSchedule, LoanService, LoanSummary } from '../../../services/loan/loan.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

interface ForeclosureQuote {
  outstandingPrincipal: number;
  accruedInterest: number;
  foreclosureCharges: number;
  processingCharges: number;
  gst: number;
  totalForeclosureAmount: number;
  futureInterestSavings: number;
}

@Component({
  selector: 'app-foreclosure',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, PageHeaderComponent],
  templateUrl: './foreclosure.component.html',
  styleUrls: ['./foreclosure.component.css']
})
export class ForeclosureComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private currentUserCustomerId: number | null = null;

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly loans = signal<LoanSummary[]>([]);
  readonly selectedLoanId = signal<number | null>(null);
  readonly selectedLoan = signal<LoanSummary | null>(null);
  readonly quote = signal<ForeclosureQuote | null>(null);
  readonly quoteReference = signal('');
  readonly quoteGeneratedAt = signal('');
  readonly schedules = signal<EmiSchedule[]>([]);

  ngOnInit(): void {
    this.initializeContext();
  }

  onLoanChange(loanIdValue: string): void {
    const loanId = Number(loanIdValue);
    if (!Number.isFinite(loanId) || loanId <= 0) {
      this.selectedLoanId.set(null);
      this.selectedLoan.set(null);
      this.quote.set(null);
      return;
    }
    this.selectedLoanId.set(loanId);
    const selected = this.loans().find((loan) => loan.loanId === loanId) || null;
    this.selectedLoan.set(selected);
    this.loadScheduleAndQuote(loanId);
  }

  generateQuote(): void {
    const loanId = this.selectedLoanId();
    if (!loanId) {
      return;
    }
    this.loadScheduleAndQuote(loanId, true);
  }

  proceedToPayment(): void {
    const selected = this.selectedLoan();
    if (!selected) {
      return;
    }
    this.router.navigate(['/emi/pay'], {
      queryParams: {
        loanId: selected.loanId,
        foreclosure: 'true'
      }
    });
  }

  resetQuote(): void {
    this.quote.set(null);
    this.quoteReference.set('');
    this.quoteGeneratedAt.set('');
  }

  formatCurrency(value: number): string {
    return `INR ${Number(value || 0).toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
  }

  private initializeContext(): void {
    if (this.canViewAllLoans()) {
      this.loadLoans();
      return;
    }

    const cached = this.authService.getCurrentUserSync();
    if (cached?.customerId) {
      this.currentUserCustomerId = cached.customerId;
      this.loadLoans();
      return;
    }

    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserCustomerId = user.customerId;
        this.loadLoans();
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to resolve user context.'
        });
      }
    });
  }

  private loadLoans(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.loanService.getLoans(0, 300).subscribe({
      next: (response) => {
        const scoped = this.canViewAllLoans()
          ? response.content
          : response.content.filter((loan) => this.currentUserCustomerId !== null && loan.customerId === this.currentUserCustomerId);
        const eligible = scoped.filter((loan) => String(loan.loanStatus || '').toUpperCase() === 'ACTIVE');
        this.loans.set(eligible);
        if (eligible.length > 0) {
          this.selectedLoanId.set(eligible[0].loanId);
          this.selectedLoan.set(eligible[0]);
          this.loadScheduleAndQuote(eligible[0].loanId);
        } else {
          this.selectedLoanId.set(null);
          this.selectedLoan.set(null);
          this.quote.set(null);
        }
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to load foreclosure eligible loans.'
        });
      }
    });
  }

  private loadScheduleAndQuote(loanId: number, refreshReference = false): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.loanService.getEmisByLoan(loanId, 0, 400).subscribe({
      next: (response) => {
        this.schedules.set(response.content);
        this.quote.set(this.computeQuote(response.content));
        if (!this.quoteReference() || refreshReference) {
          this.quoteReference.set(this.generateQuoteReference(loanId));
        }
        this.quoteGeneratedAt.set(new Date().toLocaleString('en-IN'));
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to load loan schedule for foreclosure quote.'
        });
      }
    });
  }

  private computeQuote(rows: EmiSchedule[]): ForeclosureQuote {
    const pendingRows = rows.filter((emi) => String(emi.status || '').toUpperCase() !== 'PAID');
    const outstandingPrincipal = pendingRows.reduce((sum, emi) => sum + Math.max(0, Number(emi.principalComponent) || 0), 0);
    const accruedInterest = pendingRows.reduce((sum, emi) => sum + Math.max(0, Number(emi.interestComponent) || 0), 0);
    const foreclosureCharges = outstandingPrincipal * 0.02;
    const processingCharges = outstandingPrincipal * 0.005;
    const gst = (foreclosureCharges + processingCharges) * 0.18;
    const totalForeclosureAmount = outstandingPrincipal + accruedInterest + foreclosureCharges + processingCharges + gst;
    const futureInterestSavings = Math.max(0, accruedInterest * 0.75);

    return {
      outstandingPrincipal,
      accruedInterest,
      foreclosureCharges,
      processingCharges,
      gst,
      totalForeclosureAmount,
      futureInterestSavings
    };
  }

  private canViewAllLoans(): boolean {
    return this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  private generateQuoteReference(loanId: number): string {
    const stamp = Date.now().toString(36).toUpperCase();
    return `FC-${loanId}-${stamp}`;
  }
}
