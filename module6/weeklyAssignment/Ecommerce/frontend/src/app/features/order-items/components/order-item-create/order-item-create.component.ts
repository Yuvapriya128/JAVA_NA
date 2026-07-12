import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { OrderItemRequestDTO } from '../../../../core/dto/order-item/orderitem.dto';
import { OrderItemService } from '../../../../core/services/order-item.service';

@Component({
  selector: 'app-order-item-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-item-create.component.html',
  styleUrl: './order-item-create.component.css'})
export class OrderItemCreateComponent {
  form: FormGroup;
  isSubmitting = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private orderItemService: OrderItemService
  ) {
    this.form = this.fb.group({
      orderId: ['', Validators.required],
      productId: ['', Validators.required],
      quantity: ['', [Validators.required, Validators.min(1)]],
      unitPrice: ['', [Validators.required, Validators.min(0)]]
    });
  }

  onSubmit(): void {
    console.log('[OrderItemCreateComponent] Submit clicked');
    console.log('[OrderItemCreateComponent] Form valid:', this.form.valid);
    console.log('[OrderItemCreateComponent] isSubmitting:', this.isSubmitting);
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
    const quantity = Number(this.form.get('quantity')?.value);
    const unitPrice = Number(this.form.get('unitPrice')?.value);
    const payload: OrderItemRequestDTO = {
      orderId: Number(this.form.get('orderId')?.value),
      productId: Number(this.form.get('productId')?.value),
      quantity: quantity,
      unitPrice: unitPrice,
      totalPrice: quantity * unitPrice
    };
    console.log('[OrderItemCreateComponent] Payload:', payload);

    this.orderItemService.createOrderItem(payload)
      .pipe(finalize(() => {
        this.isSubmitting = false;
      }))
      .subscribe({
      next: (response) => {
        console.log('[OrderItemCreateComponent] API Response:', response);
        this.router.navigate(['/orderitems']);
      },
      error: (error) => {
        console.error('[OrderItemCreateComponent] API Error:', error);
        this.error = this.getBackendErrorMessage(error, 'order item');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/orderitems']);
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

