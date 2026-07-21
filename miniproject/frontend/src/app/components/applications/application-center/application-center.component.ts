import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { LoanApplicationDTO, LoanProduct, LoanService } from '../../../services/loan/loan.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { AuthService } from '../../../services/auth/auth.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

type AppRole = 'USER' | 'MANAGER' | 'ADMIN';

@Component({
  selector: 'app-application-center',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink, PageHeaderComponent, StatusBadgeComponent],
  templateUrl: './application-center.component.html',
  styleUrls: ['./application-center.component.css']
})
export class ApplicationCenterComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly role = signal<AppRole>(this.resolveRole());
  readonly wizardStep = signal(1);
  readonly products = signal<LoanProduct[]>([]);
  readonly selectedProduct = signal<LoanProduct | null>(null);
  readonly emiPreview = signal<{ emiAmount: number; totalInterest: number; totalPayment: number } | null>(null);
  readonly myApplications = signal<LoanApplicationDTO[]>([]);
  readonly elevatedApplications = signal<LoanApplicationDTO[]>([]);
  readonly successMessage = signal('');
  private currentUserCustomerId: number | null = null;

  readonly wizardForm = this.fb.group({
    productCode: ['', [Validators.required]],
    principalAmount: [300000, [Validators.required, Validators.min(10000)]],
    tenureMonths: [24, [Validators.required, Validators.min(6)]]
  });

  ngOnInit(): void {
    this.loadProducts();
    if (this.role() === 'USER') {
      this.resolveCurrentUser();
      this.loadMyApplications();
      return;
    }
    this.loadElevatedApplications();
  }

  isEndUserFlow(): boolean {
    return this.role() === 'USER';
  }

  setWizardStep(step: number): void {
    this.wizardStep.set(step);
  }

  nextStep(): void {
    this.wizardStep.update((value) => Math.min(6, value + 1));
  }

  prevStep(): void {
    this.wizardStep.update((value) => Math.max(1, value - 1));
  }

  onProductChange(): void {
    const code = this.wizardForm.controls.productCode.value || '';
    const product = this.products().find((item) => item.code === code) || null;
    this.selectedProduct.set(product);
    this.emiPreview.set(null);
  }

  calculateEmi(): void {
    if (this.wizardForm.controls.productCode.invalid || this.wizardForm.controls.principalAmount.invalid || this.wizardForm.controls.tenureMonths.invalid) {
      this.wizardForm.markAllAsTouched();
      return;
    }
    const product = this.selectedProduct();
    if (!product) {
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.loanService.calculateEmi({
      principalAmount: Number(this.wizardForm.controls.principalAmount.value || 0),
      annualInterestRate: product.defaultRate,
      tenureMonths: Number(this.wizardForm.controls.tenureMonths.value || 0)
    }).subscribe({
      next: (response) => {
        this.emiPreview.set(response);
        this.status.set({ loading: false, success: false, error: '' });
        this.wizardStep.set(5);
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to calculate EMI.'
        });
      }
    });
  }

  submitApplication(): void {
    const product = this.selectedProduct();
    if (!product) {
      return;
    }

    const payload = {
      loanType: product.code,
      customerId: this.currentUserCustomerId || undefined,
      principalAmount: Number(this.wizardForm.controls.principalAmount.value || 0),
      tenureMonths: Number(this.wizardForm.controls.tenureMonths.value || 0),
      annualInterestRate: product.defaultRate
    };

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.applyForLoan(payload).subscribe({
      next: (response) => {
        this.myApplications.set([response, ...this.myApplications()]);
        this.status.set({ loading: false, success: true, error: '' });
        this.successMessage.set(`Application submitted successfully. Application ID: ${response.applicationId}`);
        this.wizardStep.set(6);
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to submit application.'
        });
      }
    });
  }

  timelineStages(application: LoanApplicationDTO): Array<{ label: string; state: 'done' | 'current' | 'upcoming' }> {
    const status = this.normalizeStatus(application.applicationStatus);
    if (status === 'APPROVED') {
      return [
        { label: 'Submitted', state: 'done' },
        { label: 'Under Review', state: 'done' },
        { label: 'Documents Verified', state: 'done' },
        { label: 'Credit Assessment', state: 'done' },
        { label: 'Approved', state: 'current' },
        { label: 'Loan Created', state: 'upcoming' },
        { label: 'Disbursed', state: 'upcoming' }
      ];
    }
    if (status === 'REJECTED') {
      return [
        { label: 'Submitted', state: 'done' },
        { label: 'Under Review', state: 'done' },
        { label: 'Documents Verified', state: 'done' },
        { label: 'Credit Assessment', state: 'current' },
        { label: 'Rejected', state: 'current' }
      ];
    }
    if (status === 'UNDER_REVIEW') {
      return [
        { label: 'Submitted', state: 'done' },
        { label: 'Under Review', state: 'current' },
        { label: 'Documents Verified', state: 'upcoming' },
        { label: 'Credit Assessment', state: 'upcoming' },
        { label: 'Approved', state: 'upcoming' },
        { label: 'Loan Created', state: 'upcoming' },
        { label: 'Disbursed', state: 'upcoming' }
      ];
    }
    return [
      { label: 'Submitted', state: 'current' },
      { label: 'Under Review', state: 'upcoming' },
      { label: 'Documents Verified', state: 'upcoming' },
      { label: 'Credit Assessment', state: 'upcoming' },
      { label: 'Approved', state: 'upcoming' },
      { label: 'Loan Created', state: 'upcoming' },
      { label: 'Disbursed', state: 'upcoming' }
    ];
  }

  pendingCount(): number {
    return this.elevatedApplications().filter((row) => this.normalizeStatus(row.applicationStatus) === 'PENDING').length;
  }

  reviewCount(): number {
    return this.elevatedApplications().filter((row) => this.normalizeStatus(row.applicationStatus) === 'UNDER_REVIEW').length;
  }

  approvedCount(): number {
    return this.elevatedApplications().filter((row) => this.normalizeStatus(row.applicationStatus) === 'APPROVED').length;
  }

  private loadProducts(): void {
    this.loanService.getLoanProducts().subscribe({
      next: (response) => {
        this.products.set(response);
        if (response.length > 0 && !this.wizardForm.controls.productCode.value) {
          this.wizardForm.patchValue({ productCode: response[0].code });
          this.selectedProduct.set(response[0]);
        }
      }
    });
  }

  private resolveCurrentUser(): void {
    const cached = this.authService.getCurrentUserSync();
    if (cached?.customerId) {
      this.currentUserCustomerId = cached.customerId;
      return;
    }
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserCustomerId = user.customerId;
      }
    });
  }

  private loadMyApplications(): void {
    this.loanService.getMyLoanApplications(0, 100).subscribe({
      next: (response) => {
        this.myApplications.set(response.content);
      }
    });
  }

  private loadElevatedApplications(): void {
    this.loanService.getLoanApplications(0, 100, 'ALL').subscribe({
      next: (response) => {
        this.elevatedApplications.set(response.content);
      }
    });
  }

  private normalizeStatus(status: string): 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED' {
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

  private resolveRole(): AppRole {
    const role = String(this.tokenStorage.getPrimaryRole() || '').replace('ROLE_', '');
    if (role === 'ADMIN' || role === 'MANAGER') {
      return role;
    }
    return 'USER';
  }
}

