import { Component, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormActionButtonsComponent } from '../../shared/buttons/form-action-buttons.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { CreateCustomerRequest, CustomerService } from '../../../services/customer/customer.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

@Component({
  selector: 'app-customer-form',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    PageHeaderComponent,
    FormActionButtonsComponent
  ],
  templateUrl: './customer-form.component.html',
  styleUrls: ['./customer-form.component.css'],
})
export class CustomerFormComponent {
  private readonly customerService = inject(CustomerService);
  private readonly fb = inject(FormBuilder);

  readonly roles = ['USER', 'MANAGER', 'ADMIN'];

  readonly customerForm = this.fb.group({
    customerName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50), Validators.pattern(/^[A-Za-z ]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    city: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(100)]],
    creditScore: [700, [Validators.required, Validators.min(300), Validators.max(900)]],
    role: ['USER', [Validators.required]]
  });

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');

  submit(): void {
    if (this.customerForm.invalid) {
      this.customerForm.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Please fix validation errors before submitting.' });
      this.successMessage.set('');
      return;
    }

    const form = this.customerForm.getRawValue();
    const payload: CreateCustomerRequest = {
      customerName: form.customerName || '',
      email: form.email || '',
      password: form.password || '',
      phoneNumber: form.phoneNumber || '',
      city: form.city || '',
      creditScore: Number(form.creditScore) || 700,
      role: (form.role as 'USER' | 'MANAGER' | 'ADMIN') || 'USER'
    };

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.customerService.createCustomer(payload).subscribe({
      next: () => {
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Customer created successfully.');
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
    this.customerForm.reset({
      customerName: '',
      email: '',
      password: '',
      phoneNumber: '',
      city: '',
      creditScore: 700,
      role: 'USER'
    });
  }

  refresh(): void {
    // Reserved for list refresh orchestration in container flows.
  }

  controlInvalid(name: keyof typeof this.customerForm.controls): boolean {
    const control = this.customerForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }
}
