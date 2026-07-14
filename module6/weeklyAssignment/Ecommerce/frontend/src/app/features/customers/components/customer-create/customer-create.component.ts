import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { CustomerRequestDTO } from '../../../../core/dto/customer/customer.dto';
import { CustomerService } from '../../../../core/services/customer.service';

@Component({
  selector: 'app-customer-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-create.component.html',
  styleUrl: './customer-create.component.css'})
export class CustomerCreateComponent {
  form: FormGroup;
  isSubmitting = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private customerService: CustomerService
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      phoneNumber: ['', [Validators.pattern(/^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/)]]
    });
  }

  onSubmit(): void {
    console.log('[CustomerCreateComponent] Submit clicked');
    console.log('[CustomerCreateComponent] Form valid:', this.form.valid);
    console.log('[CustomerCreateComponent] isSubmitting:', this.isSubmitting);
    if (this.isSubmitting) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error = 'Please fix the validation errors before submitting.';
      return;
    }

    this.isSubmitting = true;
    this.error = '';
    const payload: CustomerRequestDTO = {
      name: (this.form.get('name')?.value ?? '').trim(),
      email: (this.form.get('email')?.value ?? '').trim(),
      address: (this.form.get('address')?.value ?? '').trim(),
      password: this.form.get('password')?.value ?? '',
      phoneNumber: (this.form.get('phoneNumber')?.value ?? '').trim() || undefined
    };
    console.log('[CustomerCreateComponent] Payload:', payload);

    this.customerService.createCustomer(payload)
      .pipe(finalize(() => {
        this.isSubmitting = false;
      }))
      .subscribe({
      next: (response) => {
        console.log('[CustomerCreateComponent] API Response:', response);
        this.router.navigate(['/customers']);
      },
      error: (error) => {
        console.error('[CustomerCreateComponent] API Error:', error);
        this.error = this.getBackendErrorMessage(error, 'customer');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/customers']);
  }

  private getBackendErrorMessage(error: any, entity: string): string {
    const details = error?.error?.message || error?.error?.error || '';

    switch (error?.status) {
      case 400:
        return details || `Invalid ${entity} data. Please review all fields.`;
      case 401:
        return 'Your session has expired. Please log in again.';
      case 403:
        return `You do not have permission to create ${entity}s.`;
      case 404:
        return 'Required API endpoint was not found.';
      case 409:
        return details || `A ${entity} with the same information already exists.`;
      case 500:
        return 'Server error occurred while creating the record. Please try again.';
      default:
        return details || `Error creating ${entity}.`;
    }
  }
}

