import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { OrderRequestDTO } from '../../../../core/dto/order/order.dto';
import { OrderService } from '../../../../core/services/order.service';
import { AuthStateService } from '../../../../core/auth/auth-state.service';

@Component({
  selector: 'app-order-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-create.component.html',
  styleUrl: './order-create.component.css'})
export class OrderCreateComponent {
  form: FormGroup;
  isSubmitting = false;
  error = '';
  fieldErrors: string[] = [];

  private readonly authState = inject(AuthStateService);

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private orderService: OrderService
  ) {
    this.form = this.fb.group({
      customerId: ['', Validators.required],
      productId: ['', Validators.required],
      quantity: ['', [Validators.required, Validators.min(1)]],
      totalAmount: ['', [Validators.required, Validators.min(0)]],
      status: ['', Validators.required],
      orderDate: ['', Validators.required]
    });

    const currentUserId = this.getCurrentUserId();
    if (this.isUserRole() && currentUserId) {
      this.form.patchValue({ customerId: currentUserId });
      this.form.get('customerId')?.disable({ emitEvent: false });
    }
  }

   onSubmit(): void {
     console.log('[OrderCreateComponent] Submit clicked');
     console.log('[OrderCreateComponent] Form valid:', this.form.valid);
     console.log('[OrderCreateComponent] isSubmitting:', this.isSubmitting);
     if (this.isSubmitting) {
       return;
     }

     if (this.form.invalid) {
       this.form.markAllAsTouched();
       this.error = 'Please fix the validation errors before submitting.';
       this.fieldErrors = [];
       return;
     }

     this.isSubmitting = true;
     this.error = '';
     this.fieldErrors = [];
     const dateValue = this.form.get('orderDate')?.value;
     // Ensure date is in ISO format (YYYY-MM-DD or YYYY-MM-DDTHH:mm:ss.SSSZ)
     const formattedDate = dateValue ? (typeof dateValue === 'string' ? dateValue : new Date(dateValue).toISOString()) : '';

     const payload: OrderRequestDTO = {
       customerId: this.isUserRole()
         ? (this.getCurrentUserId() ?? 0)
         : Number(this.form.get('customerId')?.value),
       productId: Number(this.form.get('productId')?.value),
       quantity: Number(this.form.get('quantity')?.value),
       totalAmount: Number(this.form.get('totalAmount')?.value),
       status: (this.form.get('status')?.value ?? '').trim(),
       orderDate: formattedDate
     };
     console.log('[OrderCreateComponent] Payload:', payload);

     this.orderService.createOrder(payload)
       .pipe(finalize(() => {
         this.isSubmitting = false;
       }))
       .subscribe({
       next: (response) => {
         console.log('[OrderCreateComponent] API Response:', response);
         this.router.navigate(['/orders']);
       },
       error: (error) => {
         console.error('[OrderCreateComponent] API Error:', error);
         this.error = this.getBackendErrorMessage(error, 'order');
          this.fieldErrors = this.extractFieldErrors(error);
       }
     });
   }

  onCancel(): void {
    this.router.navigate(['/orders']);
  }

  private getBackendErrorMessage(error: any, entity: string): string {
    const details = error?.error?.message || error?.error?.error || '';

    switch (error?.status) {
      case 400:
        return details || `Invalid ${entity} data. Please review all fields.`;
      case 401:
        return 'Your session has expired. Please log in again.';
      case 403:
        return details || `You do not have permission to create ${entity}s.`;
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

  private extractFieldErrors(error: any): string[] {
    const backendErrors = error?.error;
    if (!backendErrors || typeof backendErrors !== 'object') {
      return [];
    }

    const fieldErrorEntries = Object.entries(backendErrors)
      .filter(([key, value]) => key !== 'message' && typeof value === 'string')
      .map(([key, value]) => `${key}: ${value}`);

    return fieldErrorEntries;
  }

  isUserRole(): boolean {
    return this.authState.isUser();
  }

  getCurrentUserId(): number | null {
    const user = this.authState.currentUser();
    const id = Number(user?.customerId);
    return Number.isFinite(id) && id > 0 ? id : null;
  }
}
