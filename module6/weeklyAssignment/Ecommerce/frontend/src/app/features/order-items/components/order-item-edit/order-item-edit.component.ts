import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { OrderItemService } from '../../../../core/services/order-item.service';

@Component({
  selector: 'app-order-item-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-item-edit.component.html',
  styleUrl: './order-item-edit.component.css'})
export class OrderItemEditComponent implements OnInit {
  form: FormGroup;
  isLoading = true;
  isSubmitting = false;
  error = '';
  itemId: number = 0;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
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

   ngOnInit(): void {
     console.log('[OrderItemEditComponent] Edit Component Loaded');
     const id = this.route.snapshot.paramMap.get('id');
     console.log('[OrderItemEditComponent] Extracted ID from snapshot:', id);

     if (!id || isNaN(+id) || +id <= 0) {
       console.warn('[OrderItemEditComponent] Invalid ID:', id);
       this.isLoading = false;
       this.error = 'Invalid order item ID';
       return;
     }

     this.itemId = +id;
     this.loadOrderItem();
   }

   loadOrderItem(): void {
     // Validate route parameter
     if (!this.itemId || isNaN(this.itemId) || this.itemId <= 0) {
       console.warn('[OrderItemEditComponent] Invalid ID:', this.itemId);
       this.isLoading = false;
       this.error = 'Invalid order item ID';
       return;
     }

     console.log('[OrderItemEditComponent] Calling getById:', this.itemId);
     this.orderItemService.getOrderItemById(this.itemId)
       .pipe(
         finalize(() => {
           this.isLoading = false;
         })
       )
       .subscribe({
         next: (data) => {
           console.log('[OrderItemEditComponent] API Response:', data);
           this.form.patchValue({
             orderId: data.orderId,
             productId: data.productId,
             quantity: data.quantity,
             unitPrice: data.unitPrice
           });
           this.error = '';
           console.log('[OrderItemEditComponent] Form patched successfully');
         },
         error: (error) => {
           console.error('[OrderItemEditComponent] API Error:', error);
           if (error.status === 404) {
             this.error = 'Order item not found';
           } else if (error.status === 403) {
             this.error = 'Access denied';
           } else {
             this.error = error?.error?.message || 'Error loading order item';
           }
         }
       });
   }

  onSubmit(): void {
    if (this.isSubmitting) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.orderItemService.updateOrderItem(this.itemId, this.form.value)
      .pipe(finalize(() => {
        this.isSubmitting = false;
      }))
      .subscribe({
      next: () => {
        this.router.navigate(['/orderitems']);
      },
      error: (error) => {
        this.error = error?.error?.message || 'Error updating order item';
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/orderitems']);
  }
}

