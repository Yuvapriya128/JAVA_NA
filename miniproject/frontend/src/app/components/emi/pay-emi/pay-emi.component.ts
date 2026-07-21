import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormActionButtonsComponent } from '../../shared/buttons/form-action-buttons.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { EmiPaymentRequest, EmiPaymentResponse, EmiSchedule, LoanService, LoanSummary } from '../../../services/loan/loan.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { AuthService } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';

interface PendingEmiOption {
  emiId: number;
  installmentNumber: number;
  dueDate: string;
  amountDue: number;
  status: string;
  penaltyAmount: number;
  type: 'OVERDUE' | 'NEXT_DUE';
}

interface EmiStructureSummary {
  paidCount: number;
  nextEmiId: number | null;
  nextDueDate: string;
  overdueInterest: number;
}

interface LoanPaymentInsights {
  completedEmis: number;
  remainingEmis: number;
  totalEmis: number;
  progressPercent: number;
  nextEmiDate: string;
  nextEmiAmount: number;
  overdueAmount: number;
  penaltyAmount: number;
  interestPaid: number;
  principalPaid: number;
  remainingBalance: number;
  outstandingPrincipal: number;
}

@Component({
  selector: 'app-pay-emi',
  standalone: true,
  imports: [ReactiveFormsModule, PageHeaderComponent, FormActionButtonsComponent],
  templateUrl: './pay-emi.component.html',
  styleUrls: ['./pay-emi.component.css'],
})
export class PayEmiComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly fb = inject(FormBuilder);
  private currentUserCustomerId: number | null = null;
  private lastSubmittedReference = '';

  readonly paymentModes = ['UPI', 'NET_BANKING', 'CARD'];
  readonly allLoans = signal<LoanSummary[]>([]);
  readonly selectedCustomerId = signal<number | null>(null);
  readonly selectedLoanId = signal<number | null>(null);
  readonly selectedLoan = signal<LoanSummary | null>(null);
  readonly selectedLoanSchedules = signal<EmiSchedule[]>([]);
  readonly pendingEmis = signal<PendingEmiOption[]>([]);
  readonly emiStructure = signal<EmiStructureSummary>({
    paidCount: 0,
    nextEmiId: null,
    nextDueDate: 'N/A',
    overdueInterest: 0
  });
  readonly loanInsights = signal<LoanPaymentInsights>({
    completedEmis: 0,
    remainingEmis: 0,
    totalEmis: 0,
    progressPercent: 0,
    nextEmiDate: 'N/A',
    nextEmiAmount: 0,
    overdueAmount: 0,
    penaltyAmount: 0,
    interestPaid: 0,
    principalPaid: 0,
    remainingBalance: 0,
    outstandingPrincipal: 0
  });

  readonly paymentForm = this.fb.group({
    customerOption: ['', [Validators.required]],
    loanOption: ['', [Validators.required]],
    pendingEmiOption: ['', [Validators.required]],
    emiId: [0, [Validators.required, Validators.min(1)]],
    amount: [0, [Validators.required, Validators.min(1)]],
    paymentMode: ['UPI', [Validators.required]],
    referenceNumber: ['', [Validators.required, Validators.minLength(6)]]
  });

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');
  readonly paymentResponse = signal<EmiPaymentResponse | null>(null);

  ngOnInit(): void {
    if (!this.canViewAllCustomers()) {
      this.paymentForm.controls.customerOption.clearValidators();
      this.paymentForm.controls.customerOption.updateValueAndValidity({ emitEvent: false });
    }
    this.initializeUserContextAndLoad();
  }

  customerOptions(): string[] {
    const unique = new Map<number, string>();
    this.allLoans().forEach((loan) => {
      unique.set(loan.customerId, `#${loan.customerId} - ${loan.customerName}`);
    });
    return Array.from(unique.values());
  }

  loanOptions(): string[] {
    const customerId = this.selectedCustomerId();
    if (!customerId) {
      return [];
    }

    return this.allLoans()
      .filter((loan) => loan.customerId === customerId)
      .map((loan) => `#${loan.loanId} - ${loan.loanType} (${loan.loanStatus})`);
  }

  onCustomerChange(option: string): void {
    const selected = Number(option.split(' - ')[0].replace('#', ''));
    this.selectedCustomerId.set(Number.isNaN(selected) ? null : selected);
    this.selectedLoanId.set(null);
    this.pendingEmis.set([]);
    this.emiStructure.set({
      paidCount: 0,
      nextEmiId: null,
      nextDueDate: 'N/A',
      overdueInterest: 0
    });
    this.paymentForm.patchValue({
      loanOption: '',
      pendingEmiOption: '',
      emiId: 0,
      amount: 0,
      referenceNumber: ''
    });
    this.applyDefaultLoanSelection();
  }

  onLoanChange(option: string): void {
    const selected = Number(option.split(' - ')[0].replace('#', ''));
    const loanId = Number.isNaN(selected) ? null : selected;
    this.selectedLoanId.set(loanId);
    this.selectedLoan.set(this.allLoans().find((loan) => loan.loanId === loanId) || null);

    if (loanId) {
      this.loadPendingEmisForLoan(loanId);
    } else {
      this.selectedLoanSchedules.set([]);
      this.pendingEmis.set([]);
      this.emiStructure.set({
        paidCount: 0,
        nextEmiId: null,
        nextDueDate: 'N/A',
        overdueInterest: 0
      });
      this.loanInsights.set(this.defaultLoanInsights());
    }
    this.paymentForm.patchValue({
      pendingEmiOption: '',
      emiId: 0,
      amount: 0,
      referenceNumber: ''
    });
  }

  onPendingEmiChange(option: string): void {
    const selected = Number(option);
    const pendingEmi = this.pendingEmis().find((item) => item.emiId === selected);
    if (!pendingEmi) {
      return;
    }

    this.paymentForm.patchValue({
      emiId: pendingEmi.emiId,
      amount: pendingEmi.amountDue,
      referenceNumber: this.generateReferenceNumber()
    });
  }

  submit(): void {
    if (this.paymentForm.invalid) {
      this.paymentForm.markAllAsTouched();
      this.status.set({
        loading: false,
        success: false,
        error: this.canSelectCustomer()
          ? 'Select customer, loan, and pending EMI with valid payment details.'
          : 'Select loan and pending EMI with valid payment details.'
      });
      this.successMessage.set('');
      return;
    }

    const form = this.paymentForm.getRawValue();
    let referenceNumber = String(form.referenceNumber || '').trim();
    if (!referenceNumber || referenceNumber === this.lastSubmittedReference) {
      referenceNumber = this.generateReferenceNumber();
      this.paymentForm.patchValue({ referenceNumber });
    }

    const payload: EmiPaymentRequest = {
      emiId: Number(form.emiId) || 0,
      amount: Number(form.amount) || 0,
      paymentMode: form.paymentMode || 'UPI',
      referenceNumber
    };
    this.lastSubmittedReference = referenceNumber;

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.payEmi(payload).subscribe({
      next: (response) => {
        this.paymentResponse.set(response);
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Payment recorded successfully.');
        this.lastSubmittedReference = '';
        this.reset();
      },
      error: (error) => {
        const normalizedError = String(
          error?.error?.message ||
          error?.message ||
          ''
        ).toLowerCase();

        if (normalizedError.includes('duplicate payment reference')) {
          this.lastSubmittedReference = '';
          this.paymentForm.patchValue({
            referenceNumber: this.generateReferenceNumber()
          });
        }

        this.status.set({
          loading: false,
          success: false,
          error: this.mapPayEmiErrorMessage(error)
        });
        this.successMessage.set('');
      }
    });
  }

  reset(): void {
    this.selectedCustomerId.set(null);
    this.selectedLoanId.set(null);
    this.pendingEmis.set([]);
    this.selectedLoan.set(null);
    this.selectedLoanSchedules.set([]);
    this.emiStructure.set({
      paidCount: 0,
      nextEmiId: null,
      nextDueDate: 'N/A',
      overdueInterest: 0
    });
    this.loanInsights.set(this.defaultLoanInsights());
    this.paymentForm.reset({
      customerOption: '',
      loanOption: '',
      pendingEmiOption: '',
      emiId: 0,
      amount: 0,
      paymentMode: 'UPI',
      referenceNumber: ''
    });
    this.lastSubmittedReference = '';
    this.applyDefaultCustomerSelection();
  }

  controlInvalid(name: keyof typeof this.paymentForm.controls): boolean {
    const control = this.paymentForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  private loadLoans(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.loanService.getLoans(0, 200).subscribe({
      next: (response) => {
        const scopedLoans = this.canViewAllCustomers()
          ? response.content
          : response.content.filter((loan) =>
            this.currentUserCustomerId !== null &&
            loan.customerId === this.currentUserCustomerId &&
            this.isActiveLoan(loan)
          );
        this.allLoans.set(scopedLoans);
        this.status.set({ loading: false, success: false, error: '' });
        this.applyDefaultCustomerSelection();
      },
      error: () => {
        this.status.set({
          loading: false,
          success: false,
          error: 'Unable to load loans for payment workflow.'
        });
        this.successMessage.set('');
      }
    });
  }

  private initializeUserContextAndLoad(): void {
    if (this.canViewAllCustomers()) {
      this.loadLoans();
      return;
    }

    const cachedUser = this.authService.getCurrentUserSync();
    if (cachedUser?.customerId) {
      this.currentUserCustomerId = cachedUser.customerId;
      this.loadLoans();
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserCustomerId = user.customerId;
        this.loadLoans();
      },
      error: () => {
        this.status.set({
          loading: false,
          success: false,
          error: 'Unable to resolve current user profile.'
        });
      }
    });
  }

  private canViewAllCustomers(): boolean {
    return this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  canSelectCustomer(): boolean {
    return this.canViewAllCustomers();
  }

  private applyDefaultCustomerSelection(): void {
    if (this.canViewAllCustomers()) {
      return;
    }

    const options = this.customerOptions();
    if (options.length !== 1) {
      return;
    }

    const defaultCustomer = options[0];
    this.paymentForm.patchValue({ customerOption: defaultCustomer });
    this.onCustomerChange(defaultCustomer);
  }

  private applyDefaultLoanSelection(): void {
    if (this.canViewAllCustomers()) {
      return;
    }

    const options = this.loanOptions();
    if (options.length === 0) {
      this.pendingEmis.set([]);
      this.selectedLoanSchedules.set([]);
      this.emiStructure.set({
        paidCount: 0,
        nextEmiId: null,
        nextDueDate: 'N/A',
        overdueInterest: 0
      });
      this.loanInsights.set(this.defaultLoanInsights());
      return;
    }

    if (options.length === 1) {
      const defaultLoan = options[0];
      this.paymentForm.patchValue({ loanOption: defaultLoan });
      this.onLoanChange(defaultLoan);
    }
  }

  private loadPendingEmisForLoan(loanId: number): void {
    this.loanService.getEmisByLoan(loanId).subscribe({
      next: (response) => {
        const pending = this.buildPendingEmiOptions(response.content);
        const summary = this.buildEmiStructureSummary(response.content, pending);
        const insights = this.buildLoanInsights(response.content);
        this.selectedLoanSchedules.set(response.content);
        this.pendingEmis.set(pending);
        this.emiStructure.set(summary);
        this.loanInsights.set(insights);

        if (!this.canViewAllCustomers() && pending.length > 0) {
          const defaultPendingOption = String(pending[0].emiId);
          this.paymentForm.patchValue({ pendingEmiOption: defaultPendingOption });
          this.onPendingEmiChange(defaultPendingOption);
        }
      },
      error: () => {
        this.pendingEmis.set([]);
        this.selectedLoanSchedules.set([]);
        this.emiStructure.set({
          paidCount: 0,
          nextEmiId: null,
          nextDueDate: 'N/A',
          overdueInterest: 0
        });
        this.loanInsights.set(this.defaultLoanInsights());
        this.status.set({
          loading: false,
          success: false,
          error: 'Unable to load pending EMI options for this loan.'
        });
      }
    });
  }

  private isPendingEmi(emi: EmiSchedule): boolean {
    const status = String(emi.status || '').toUpperCase();
    const amountDue = this.getPayableAmount(emi);
    const validStatus = status === 'PENDING' || status === 'OVERDUE' || status === 'DUE';
    return validStatus && amountDue > 0;
  }

  private isActiveLoan(loan: LoanSummary): boolean {
    return String(loan.loanStatus || '').trim().toUpperCase() === 'ACTIVE';
  }

  private toPendingEmiOption(emi: EmiSchedule): PendingEmiOption {
    return {
      emiId: emi.emiId,
      installmentNumber: emi.installmentNumber,
      dueDate: emi.dueDate,
      amountDue: this.getPayableAmount(emi),
      status: emi.status,
      penaltyAmount: Math.max(0, Number(emi.penaltyAmount) || 0),
      type: 'NEXT_DUE'
    };
  }

  pendingEmiLabel(emi: PendingEmiOption): string {
    const typeLabel = emi.type === 'OVERDUE' ? 'Overdue EMI' : 'Next EMI';
    const overdueInterest = emi.penaltyAmount > 0 ? ` | Overdue Interest INR ${this.formatInr(emi.penaltyAmount)}` : '';
    return `EMI #${emi.emiId} (Inst ${emi.installmentNumber}) | ${typeLabel} | Due ${this.formatDate(emi.dueDate)} | INR ${this.formatInr(emi.amountDue)}${overdueInterest}`;
  }

  selectedPendingEmi(): PendingEmiOption | null {
    const selected = Number(this.paymentForm.controls.pendingEmiOption.value || 0);
    if (!selected) {
      return null;
    }
    return this.pendingEmis().find((item) => item.emiId === selected) || null;
  }

  private getPayableAmount(emi: EmiSchedule): number {
    const amountDue = Math.max(0, Number(emi.amountDue) || 0);
    if (amountDue > 0) {
      return amountDue;
    }

    const principal = Math.max(0, Number(emi.principalComponent) || 0);
    const interest = Math.max(0, Number(emi.interestComponent) || 0);
    const penalty = Math.max(0, Number(emi.penaltyAmount) || 0);
    const paid = Math.max(0, Number(emi.amountPaid) || 0);
    return Math.max(0, principal + interest + penalty - paid);
  }

  private buildPendingEmiOptions(schedules: EmiSchedule[]): PendingEmiOption[] {
    const payableRows = schedules
      .filter((emi) => this.isPendingEmi(emi))
      .sort((left, right) => this.toDateValue(left.dueDate) - this.toDateValue(right.dueDate));

    const todayStart = new Date();
    todayStart.setHours(0, 0, 0, 0);
    const todayValue = todayStart.getTime();

    const overdueRows = payableRows.filter((emi) =>
      this.toDateValue(emi.dueDate) < todayValue || String(emi.status || '').toUpperCase() === 'OVERDUE'
    );
    const upcomingRows = payableRows.filter((emi) => !overdueRows.some((row) => row.emiId === emi.emiId));
    const nextDue = upcomingRows[0] || null;

    const options: PendingEmiOption[] = overdueRows.map((emi) => ({
      ...this.toPendingEmiOption(emi),
      type: 'OVERDUE'
    }));

    if (nextDue) {
      options.push({
        ...this.toPendingEmiOption(nextDue),
        type: 'NEXT_DUE'
      });
    } else if (options.length === 0 && payableRows.length > 0) {
      options.push({
        ...this.toPendingEmiOption(payableRows[0]),
        type: 'NEXT_DUE'
      });
    }

    return options;
  }

  private buildEmiStructureSummary(schedules: EmiSchedule[], pendingOptions: PendingEmiOption[]): EmiStructureSummary {
    const paidCount = schedules.filter((emi) => this.isPaidEmi(emi)).length;
    const nextDue = pendingOptions.find((emi) => emi.type === 'NEXT_DUE') || null;
    const overdueInterest = pendingOptions
      .filter((emi) => emi.type === 'OVERDUE')
      .reduce((sum, emi) => sum + emi.penaltyAmount, 0);

    return {
      paidCount,
      nextEmiId: nextDue?.emiId ?? null,
      nextDueDate: nextDue ? this.formatDate(nextDue.dueDate) : 'N/A',
      overdueInterest
    };
  }

  private buildLoanInsights(schedules: EmiSchedule[]): LoanPaymentInsights {
    const total = schedules.length;
    const completedRows = schedules.filter((emi) => this.isPaidEmi(emi));
    const completedEmis = completedRows.length;
    const remainingEmis = Math.max(0, total - completedEmis);
    const progressPercent = total > 0 ? Math.round((completedEmis / total) * 100) : 0;
    const pendingRows = schedules
      .filter((emi) => !this.isPaidEmi(emi))
      .sort((left, right) => this.toDateValue(left.dueDate) - this.toDateValue(right.dueDate));
    const nextEmi = pendingRows[0] || null;
    const overdueRows = schedules.filter((emi) => {
      const status = String(emi.status || '').toUpperCase();
      return status === 'OVERDUE' || (status !== 'PAID' && this.toDateValue(emi.dueDate) < this.todayStartValue());
    });
    const overdueAmount = overdueRows.reduce((sum, emi) => sum + this.getPayableAmount(emi), 0);
    const penaltyAmount = overdueRows.reduce((sum, emi) => sum + Math.max(0, Number(emi.penaltyAmount) || 0), 0);
    const interestPaid = completedRows.reduce((sum, emi) => sum + Math.max(0, Number(emi.interestComponent) || 0), 0);
    const principalPaid = completedRows.reduce((sum, emi) => sum + Math.max(0, Number(emi.principalComponent) || 0), 0);
    const totalScheduledAmount = schedules.reduce((sum, emi) => sum + Math.max(0, Number(emi.principalComponent) || 0) + Math.max(0, Number(emi.interestComponent) || 0), 0);
    const totalPaid = completedRows.reduce((sum, emi) => sum + Math.max(0, Number(emi.amountPaid) || 0), 0);
    const remainingBalance = Math.max(0, totalScheduledAmount - totalPaid);
    const selectedLoan = this.selectedLoan();
    const outstandingPrincipal = typeof selectedLoan?.outstandingAmount === 'number'
      ? Math.max(0, selectedLoan.outstandingAmount)
      : remainingBalance;

    return {
      completedEmis,
      remainingEmis,
      totalEmis: total,
      progressPercent,
      nextEmiDate: nextEmi ? this.formatDate(nextEmi.dueDate) : 'N/A',
      nextEmiAmount: nextEmi ? this.getPayableAmount(nextEmi) : 0,
      overdueAmount,
      penaltyAmount,
      interestPaid,
      principalPaid,
      remainingBalance,
      outstandingPrincipal
    };
  }

  private isPaidEmi(emi: EmiSchedule): boolean {
    const status = String(emi.status || '').toUpperCase();
    return status === 'PAID' || this.getPayableAmount(emi) <= 0;
  }

  private toDateValue(value: string): number {
    const parsed = new Date(value).getTime();
    if (Number.isNaN(parsed)) {
      return Number.MAX_SAFE_INTEGER;
    }
    return parsed;
  }

  private todayStartValue(): number {
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    return today.getTime();
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

  private formatInr(value: number): string {
    return Number(value || 0).toLocaleString('en-IN', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  private mapPayEmiErrorMessage(error: unknown): string {
    const raw = String(
      (error as { error?: { message?: string }; message?: string })?.error?.message ||
      (error as { message?: string })?.message ||
      ''
    );
    const normalized = raw.toLowerCase();
    if (normalized.includes('emi schedule not found')) {
      return 'Selected EMI is not available for payment. Please reselect the loan and choose a pending EMI again.';
    }
    if (normalized.includes('already paid')) {
      return 'This EMI is already paid. Please choose another pending EMI.';
    }
    if (normalized.includes('duplicate payment reference')) {
      return 'Reference number already used. A new reference has been generated automatically. Please submit again.';
    }
    return raw || 'Something went wrong.';
  }

  private generateReferenceNumber(): string {
    const timestamp = Date.now().toString(36).toUpperCase();
    const random = this.generateRandomAlphaNumeric(8);
    return `PAY-${timestamp}-${random}`;
  }

  private generateRandomAlphaNumeric(length: number): string {
    const alphabet = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789';
    const cryptoObject = globalThis.crypto;
    if (cryptoObject && typeof cryptoObject.getRandomValues === 'function') {
      const randomValues = new Uint32Array(length);
      cryptoObject.getRandomValues(randomValues);
      return Array.from(randomValues, (value) => alphabet[value % alphabet.length]).join('');
    }

    let fallback = '';
    for (let i = 0; i < length; i += 1) {
      fallback += alphabet[Math.floor(Math.random() * alphabet.length)];
    }
    return fallback;
  }

  private toNumber(value: number | string): number {
    if (typeof value === 'number') {
      return value;
    }
    const parsed = Number(String(value).replace(/[^\d.-]/g, ''));
    return Number.isFinite(parsed) ? parsed : 0;
  }

  private defaultLoanInsights(): LoanPaymentInsights {
    return {
      completedEmis: 0,
      remainingEmis: 0,
      totalEmis: 0,
      progressPercent: 0,
      nextEmiDate: 'N/A',
      nextEmiAmount: 0,
      overdueAmount: 0,
      penaltyAmount: 0,
      interestPaid: 0,
      principalPaid: 0,
      remainingBalance: 0,
      outstandingPrincipal: 0
    };
  }
}
