import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { CustomerResponse, CustomerService, UpdateCustomerRequest } from '../../../services/customer/customer.service';
import { EmiPaymentHistory, LoanApplicationDTO, LoanService, LoanSummary } from '../../../services/loan/loan.service';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

@Component({
  selector: 'app-customer-details',
  standalone: true,
  imports: [ReactiveFormsModule, PageHeaderComponent, StatusBadgeComponent],
  templateUrl: './customer-details.component.html',
  styleUrls: ['./customer-details.component.css'],
})
export class CustomerDetailsComponent implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly loanService = inject(LoanService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly customer = signal<CustomerResponse | null>(null);
  readonly editMode = signal(false);
  readonly activeTab = signal<'overview' | 'loans' | 'applications' | 'payments' | 'notifications' | 'audit'>('overview');
  readonly customerLoans = signal<LoanSummary[]>([]);
  readonly customerApplications = signal<LoanApplicationDTO[]>([]);
  readonly customerPayments = signal<EmiPaymentHistory[]>([]);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');

  readonly form = this.fb.group({
    customerName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50), Validators.pattern(/^[A-Za-z ]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    city: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    creditScore: [700, [Validators.required, Validators.min(300), Validators.max(900)]],
    role: ['USER', [Validators.required]],
    active: [true]
  });

  readonly roles = ['USER', 'MANAGER', 'ADMIN'];

  ngOnInit(): void {
    const mode = this.route.snapshot.queryParamMap.get('mode');
    this.editMode.set(mode === 'edit');

    this.loadData();
  }

  loadData(): void {
    const customerId = Number(this.route.snapshot.queryParamMap.get('id'));
    if (!Number.isFinite(customerId) || customerId <= 0) {
      this.status.set({
        loading: false,
        success: false,
        error: 'Customer ID is missing or invalid.'
      });
      this.successMessage.set('');
      return;
    }

    this.status.set({ loading: true, success: this.status().success, error: '' });
    this.customerService.getCustomer(customerId).subscribe({
      next: (response) => {
        this.customer.set(response);
        this.form.patchValue({
          customerName: response.customerName,
          email: response.email,
          phoneNumber: response.phoneNumber,
          city: response.city,
          creditScore: response.creditScore,
          role: response.role,
          active: response.active
        });
        this.loadCustomer360Data(response);
        this.status.set({ loading: false, success: this.status().success, error: '' });
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

  refresh(): void {
    this.loadData();
  }

  saveChanges(): void {
    const customer = this.customer();
    if (!customer) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Please fix validation errors before saving changes.' });
      this.successMessage.set('');
      return;
    }

    const value = this.form.getRawValue();
    const payload: UpdateCustomerRequest = {
      customerName: value.customerName || '',
      email: value.email || '',
      phoneNumber: value.phoneNumber || '',
      city: value.city || '',
      creditScore: Number(value.creditScore) || 700,
      role: (value.role as 'USER' | 'MANAGER' | 'ADMIN') || 'USER',
      active: !!value.active
    };

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.customerService.updateCustomer(customer.customerId, payload).subscribe({
      next: (response) => {
        this.customer.set(response);
        this.form.patchValue({
          customerName: response.customerName,
          email: response.email,
          phoneNumber: response.phoneNumber,
          city: response.city,
          creditScore: response.creditScore,
          role: response.role,
          active: response.active
        });
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Customer details updated successfully.');
      },
      error: (error) => {
        const rawMessage = String(error?.error?.message || error?.message || 'Failed to update customer.');
        const message = error?.status === 500
          ? 'Customer update API failed on server. Please verify backend update endpoint/validation contract for customer edits.'
          : rawMessage;
        this.status.set({
          loading: false,
          success: false,
          error: message
        });
        this.successMessage.set('');
      }
    });
  }

  cancelEdit(): void {
    const customer = this.customer();
    if (customer) {
      this.form.patchValue({
        customerName: customer.customerName,
        email: customer.email,
        phoneNumber: customer.phoneNumber,
        city: customer.city,
        creditScore: customer.creditScore,
        role: customer.role,
        active: customer.active
      });
    }

    this.router.navigate(['/customers/details'], {
      queryParams: { id: customer?.customerId || this.route.snapshot.queryParamMap.get('id') }
    });
  }

  controlInvalid(name: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  setActiveTab(tab: 'overview' | 'loans' | 'applications' | 'payments' | 'notifications' | 'audit'): void {
    this.activeTab.set(tab);
  }

  activeLoansCount(): number {
    return this.customerLoans().filter((loan) => String(loan.loanStatus || '').toUpperCase() === 'ACTIVE').length;
  }

  totalOutstanding(): number {
    return this.customerLoans().reduce((sum, loan) => sum + Math.max(0, Number(loan.outstandingAmount) || 0), 0);
  }

  totalPaidAmount(): number {
    return this.customerPayments().reduce((sum, row) => sum + Math.max(0, Number(row.amountPaid) || 0), 0);
  }

  formatCurrency(value: number): string {
    return `INR ${Number(value || 0).toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
  }

  private loadCustomer360Data(customer: CustomerResponse): void {
    this.loadCustomerLoans(customer.customerId);
    this.loadCustomerApplications(customer.customerId);
    this.loadCustomerPayments(customer.customerId, customer.customerName, customer.email);
  }

  private loadCustomerLoans(customerId: number): void {
    this.loanService.getLoans(0, 300).subscribe({
      next: (response) => {
        this.customerLoans.set(response.content.filter((loan) => loan.customerId === customerId));
      },
      error: () => {
        this.customerLoans.set([]);
      }
    });
  }

  private loadCustomerApplications(customerId: number): void {
    this.loanService.getLoanApplications(0, 300, 'ALL').subscribe({
      next: (response) => {
        this.customerApplications.set(response.content.filter((row) => row.customerId === customerId));
      },
      error: () => {
        this.customerApplications.set([]);
      }
    });
  }

  private loadCustomerPayments(customerId: number, customerName: string, email: string): void {
    this.loanService.getEmiPayments(0, 500).subscribe({
      next: (response) => {
        const rows = Array.isArray(response) ? response : response.content;
        const normalizedName = String(customerName || '').trim().toLowerCase();
        const normalizedEmail = String(email || '').trim().toLowerCase();
        this.customerPayments.set(rows.filter((row) => {
          const rowAny = row as unknown as { customerId?: number; customerName?: string; customerEmail?: string };
          if (typeof rowAny.customerId === 'number') {
            return rowAny.customerId === customerId;
          }
          if (String(rowAny.customerEmail || '').trim().toLowerCase() === normalizedEmail && normalizedEmail) {
            return true;
          }
          return String(rowAny.customerName || '').trim().toLowerCase() === normalizedName && normalizedName.length > 0;
        }));
      },
      error: () => {
        this.customerPayments.set([]);
      }
    });
  }
}
