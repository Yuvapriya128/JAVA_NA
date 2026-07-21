import { Component, OnDestroy, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormActionButtonsComponent } from '../../shared/buttons/form-action-buttons.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { CreateLoanRequest, EmiCalculationResponse, LoanService } from '../../../services/loan/loan.service';
import { LOAN_TYPE_OPTIONS, normalizeLoanType } from '../../../constants/loan-types';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

@Component({
  selector: 'app-create-loan',
  standalone: true,
  imports: [ReactiveFormsModule, PageHeaderComponent, FormActionButtonsComponent],
  templateUrl: './create-loan.component.html',
  styleUrls: ['./create-loan.component.css'],
})
export class CreateLoanComponent implements OnInit, OnDestroy {
  private readonly loanService = inject(LoanService);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);
  private calculationTimer: ReturnType<typeof setTimeout> | null = null;
  private calculationRequestId = 0;

  readonly loanTypes = LOAN_TYPE_OPTIONS.map((item) => item.code);
  readonly sourceApplicationId = signal<number | null>(null);
  readonly createdFromSourceApplication = signal(false);

  readonly loanForm = this.fb.group({
    loanType: ['HOME', [Validators.required]],
    customerId: [0, [Validators.required, Validators.min(1)]],
    principalAmount: [0, [Validators.required, Validators.min(1)]],
    annualInterestRate: [0, [Validators.required, Validators.min(0.01), Validators.max(100)]],
    tenureMonths: [0, [Validators.required, Validators.min(1)]]
  });

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');

  readonly emiPreview = signal<{
    loading: boolean;
    error: string;
    data: EmiCalculationResponse | null;
  }>({
    loading: false,
    error: '',
    data: null
  });

  ngOnInit(): void {
    this.activatedRoute.queryParams.subscribe((params) => {
      this.loanForm.patchValue({
        loanType: params['loanType'] ? normalizeLoanType(String(params['loanType'])) : this.loanForm.controls.loanType.value,
        customerId: this.toPositive(params['customerId'], this.loanForm.controls.customerId.value || 0),
        principalAmount: this.toPositive(params['principalAmount'], this.loanForm.controls.principalAmount.value || 0),
        annualInterestRate: this.toPositive(params['annualInterestRate'], this.loanForm.controls.annualInterestRate.value || 0),
        tenureMonths: this.toPositive(params['tenureMonths'], this.loanForm.controls.tenureMonths.value || 0)
      }, { emitEvent: false });

      const applicationId = Number(params['applicationId']);
      if (Number.isFinite(applicationId) && applicationId > 0) {
        this.sourceApplicationId.set(applicationId);
        this.createdFromSourceApplication.set(false);
      } else {
        this.sourceApplicationId.set(null);
        this.createdFromSourceApplication.set(false);
      }

      this.scheduleAutoCalculate();
    });

    this.loanForm.controls.principalAmount.valueChanges.subscribe(() => this.scheduleAutoCalculate());
    this.loanForm.controls.annualInterestRate.valueChanges.subscribe(() => this.scheduleAutoCalculate());
    this.loanForm.controls.tenureMonths.valueChanges.subscribe(() => this.scheduleAutoCalculate());
  }

  ngOnDestroy(): void {
    this.clearCalculationTimer();
  }

  submit(): void {
    if (this.status().loading) {
      return;
    }

    if (this.sourceApplicationId() && this.createdFromSourceApplication()) {
      this.status.set({
        loading: false,
        success: false,
        error: 'Loan already created for this approved request in this session. Avoid duplicate submissions.'
      });
      this.successMessage.set('');
      return;
    }

    if (this.loanForm.invalid) {
      this.loanForm.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Please fix validation errors before creating a loan.' });
      this.successMessage.set('');
      return;
    }

    const form = this.loanForm.getRawValue();
    const payload: CreateLoanRequest = {
      loanType: form.loanType || 'HOME',
      customerId: Number(form.customerId) || 0,
      principalAmount: Number(form.principalAmount) || 0,
      annualInterestRate: Number(form.annualInterestRate) || 0,
      tenureMonths: Number(form.tenureMonths) || 0
    };

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.createLoan(payload).subscribe({
      next: () => {
        if (this.sourceApplicationId()) {
          this.createdFromSourceApplication.set(true);
        }
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Loan created successfully.');
        this.reset();
        this.refresh();
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

  reset(): void {
    this.loanForm.reset({
      loanType: 'HOME',
      customerId: 0,
      principalAmount: 0,
      annualInterestRate: 0,
      tenureMonths: 0
    });
    this.resetPreview();
  }

  refresh(): void {
    // Reserved for parent list refresh wiring.
  }

  calculateEmi(): void {
    if (!this.hasValidCalculationInputs()) {
      this.emiPreview.set({
        loading: false,
        error: 'Enter principal amount, interest rate, and tenure to calculate EMI.',
        data: null
      });
      return;
    }

    const requestId = ++this.calculationRequestId;
    this.emiPreview.set({ loading: true, error: '', data: null });
    const form = this.loanForm.getRawValue();

    this.loanService.calculateEmi({
      principalAmount: Number(form.principalAmount) || 0,
      annualInterestRate: Number(form.annualInterestRate) || 0,
      tenureMonths: Number(form.tenureMonths) || 0
    }).subscribe({
      next: (response) => {
        if (requestId !== this.calculationRequestId) {
          return;
        }
        this.emiPreview.set({ loading: false, error: '', data: response });
      },
      error: (error) => {
        if (requestId !== this.calculationRequestId) {
          return;
        }
        this.emiPreview.set({
          loading: false,
          error: error?.error?.message || error?.message || 'Failed to calculate EMI.',
          data: null
        });
      }
    });
  }

  formatCurrency(value: number): string {
    return `INR ${value.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
  }

  controlInvalid(name: keyof typeof this.loanForm.controls): boolean {
    const control = this.loanForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  private scheduleAutoCalculate(): void {
    this.clearCalculationTimer();

    if (!this.hasValidCalculationInputs()) {
      this.resetPreview();
      return;
    }

    this.calculationTimer = setTimeout(() => {
      this.calculateEmi();
    }, 350);
  }

  private hasValidCalculationInputs(): boolean {
    const form = this.loanForm.getRawValue();
    const principal = Number(form.principalAmount) || 0;
    const rate = Number(form.annualInterestRate) || 0;
    const tenure = Number(form.tenureMonths) || 0;
    return principal > 0 && rate > 0 && rate <= 100 && tenure > 0;
  }

  private resetPreview(): void {
    this.clearCalculationTimer();
    this.emiPreview.set({ loading: false, error: '', data: null });
  }

  private clearCalculationTimer(): void {
    if (!this.calculationTimer) {
      return;
    }
    clearTimeout(this.calculationTimer);
    this.calculationTimer = null;
  }

  private toPositive(value: unknown, fallback: number): number {
    const parsed = Number(value);
    if (!Number.isFinite(parsed) || parsed <= 0) {
      return fallback;
    }
    return parsed;
  }
}
