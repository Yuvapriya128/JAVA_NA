import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { LoanService } from '../../../services/loan/loan.service';
import { LOAN_TYPE_OPTIONS, LoanTypeOption } from '../../../constants/loan-types';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

@Component({
  selector: 'app-interest-management',
  standalone: true,
  imports: [ReactiveFormsModule, PageHeaderComponent],
  templateUrl: './interest-management.component.html',
  styleUrls: ['./interest-management.component.css'],
})
export class InterestManagementComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly bulkLoanTypes: LoanTypeOption[] = LOAN_TYPE_OPTIONS.map((item) => ({
    code: item.code,
    label: item.label,
    icon: item.icon || 'bi-bank'
  }));

  selectedLoanTypes: string[] = ['HOME', 'PERSONAL'];
  readonly bulkForm = this.fb.group({
    bulkRate: [10, [Validators.required, Validators.min(0.01), Validators.max(100)]]
  });
  readonly individualForm = this.fb.group({
    loanId: [null as number | null, [Validators.required, Validators.min(1)]],
    loanRate: [null as number | null, [Validators.required, Validators.min(0.01), Validators.max(100)]]
  });

  readonly showConfirmModal = signal(false);
  readonly confirmMessage = signal('');
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');

  ngOnInit(): void {
    const loanIdParam = Number(this.route.snapshot.queryParamMap.get('loanId'));
    const rateParam = Number(this.route.snapshot.queryParamMap.get('rate'));

    if (!Number.isNaN(loanIdParam) && loanIdParam > 0) {
      this.individualForm.patchValue({ loanId: loanIdParam });
    }

    if (!Number.isNaN(rateParam) && rateParam > 0) {
      this.individualForm.patchValue({ loanRate: rateParam });
      this.status.set({
        loading: false,
        success: true,
        error: ''
      });
      this.successMessage.set(`Loan #${loanIdParam} opened for direct interest update.`);
    }
  }

  isLoanTypeSelected(code: string): boolean {
    return this.selectedLoanTypes.includes(code);
  }

  toggleLoanType(code: string): void {
    if (this.isLoanTypeSelected(code)) {
      this.selectedLoanTypes = this.selectedLoanTypes.filter((item) => item !== code);
      return;
    }
    this.selectedLoanTypes = [...this.selectedLoanTypes, code];
  }

  projectedImpactLabel(): string {
    const selected = this.selectedLoanTypes.length;
    const rate = this.bulkForm.controls.bulkRate.value || 0;
    if (selected === 0 || rate <= 0) {
      return 'Configure loan types and a valid rate to preview impact.';
    }

    const estimatedLoans = selected * 240;
    return `Estimated impacted loans: ${estimatedLoans.toLocaleString('en-IN')}`;
  }

  currentRateLabel(): string {
    return 'Current average rate: 10.25%';
  }

  newRateLabel(): string {
    const rate = this.bulkForm.controls.bulkRate.value || 0;
    if (rate <= 0) {
      return 'New proposed rate: N/A';
    }
    return `New proposed rate: ${rate.toFixed(2)}%`;
  }

  affectedLoansLabel(): string {
    const selected = this.selectedLoanTypes.length;
    if (selected === 0) {
      return 'Affected loans: N/A';
    }
    return `Affected loans (estimated): ${(selected * 240).toLocaleString('en-IN')}`;
  }

  projectedDeltaLabel(): string {
    const selected = this.selectedLoanTypes.length;
    const rate = this.bulkForm.controls.bulkRate.value || 0;
    if (selected === 0 || rate <= 0) {
      return 'Projected monthly delta: N/A';
    }

    const deltaLakhs = (selected * rate * 0.72).toFixed(1);
    return `Projected monthly delta: INR ${deltaLakhs} L`;
  }

  submitBulkPreview(): void {
    if (this.selectedLoanTypes.length === 0) {
      this.status.set({ loading: false, success: false, error: 'Select at least one loan category for bulk revision.' });
      this.successMessage.set('');
      return;
    }

    if (this.bulkForm.invalid) {
      this.bulkForm.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Enter a valid bulk interest rate between 0 and 100.' });
      this.successMessage.set('');
      return;
    }

    this.confirmMessage.set(
      `Apply ${this.bulkForm.controls.bulkRate.value}% annual interest to ${this.selectedLoanTypes.length} selected loan categories?`
    );
    this.showConfirmModal.set(true);
  }

  submitBulkUpdate(): void {
    if (this.selectedLoanTypes.length === 0 || this.bulkForm.invalid) {
      this.showConfirmModal.set(false);
      this.status.set({ loading: false, success: false, error: 'Bulk interest update form is invalid.' });
      this.successMessage.set('');
      return;
    }

    this.showConfirmModal.set(false);
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');

    this.loanService.reviseInterestRates(this.selectedLoanTypes, this.bulkForm.controls.bulkRate.value || 0).subscribe({
      next: (updatedCount) => {
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set(`Interest rates updated for ${updatedCount} loans.`);
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

  submitIndividualUpdate(): void {
    if (this.individualForm.invalid) {
      this.individualForm.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Enter valid values for loan ID and interest rate.' });
      this.successMessage.set('');
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.updateLoanInterest(
      this.individualForm.controls.loanId.value || 0,
      this.individualForm.controls.loanRate.value || 0
    ).subscribe({
      next: () => {
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Loan interest rate updated successfully.');
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

  closeConfirmModal(): void {
    this.showConfirmModal.set(false);
  }

  reset(): void {
    this.selectedLoanTypes = ['HOME', 'PERSONAL'];
    this.bulkForm.reset({ bulkRate: 10 });
    this.individualForm.reset({ loanId: null, loanRate: null });
    this.status.set({ loading: false, success: false, error: '' });
    this.successMessage.set('');
  }

  refresh(): void {
    // Reserved for future rate history refresh.
  }

  bulkInvalid(name: keyof typeof this.bulkForm.controls): boolean {
    const control = this.bulkForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  individualInvalid(name: keyof typeof this.individualForm.controls): boolean {
    const control = this.individualForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }
}
