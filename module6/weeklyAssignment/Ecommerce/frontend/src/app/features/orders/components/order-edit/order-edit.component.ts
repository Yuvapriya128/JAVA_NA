import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { OrderService } from '../../../../core/services/order.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-order-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './order-edit.component.html',
  styleUrl: './order-edit.component.css'})
export class OrderEditComponent implements OnInit, OnDestroy {
  form: FormGroup;
  isLoading = true;
  isSubmitting = false;
  error = '';
  orderId: number = 0;
  orderCustomerId = 0;
  orderTotalAmount = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      status: ['', Validators.required]
    });
  }

  ngOnInit(): void {
    console.log('[OrderEditComponent] Edit Component Loaded');
    const id = this.route.snapshot.paramMap.get('id');
    console.log('[OrderEditComponent] Extracted ID from snapshot:', id);

    if (!id || isNaN(+id) || +id <= 0) {
      console.warn('[OrderEditComponent] Invalid ID:', id);
      this.isLoading = false;
      this.error = 'Invalid order ID';
      this.cdr.detectChanges();
      return;
    }

    this.orderId = +id;
    this.loadOrder();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrder(): void {
    // Validate route parameter
    if (!this.orderId || isNaN(this.orderId) || this.orderId <= 0) {
      console.warn('[OrderEditComponent] Invalid ID:', this.orderId);
      this.isLoading = false;
      this.error = 'Invalid order ID';
      this.cdr.detectChanges();
      return;
    }

    console.log('[OrderEditComponent] Calling getById:', this.orderId);
    this.orderService.getOrderById(this.orderId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          console.log('[OrderEditComponent] API Response:', data);
          this.orderCustomerId = Number(data.customerId) || 0;
          this.orderTotalAmount = Number(data.totalAmount) || 0;
          this.form.patchValue({
            status: data.status
          });
          this.error = '';
          console.log('[OrderEditComponent] Form patched successfully');
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[OrderEditComponent] API Error:', error);
          if (error.status === 404) {
            this.error = 'Order not found';
          } else if (error.status === 403) {
            this.error = 'Access denied';
          } else {
            this.error = error?.error?.message || 'Error loading order';
          }
          this.cdr.detectChanges();
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

    const payload = {
      id: this.orderId,
      customerId: this.orderCustomerId,
      status: this.form.get('status')?.value
    };

    this.orderService.updateOrder(this.orderId, payload)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.router.navigate(['/orders']);
        },
        error: (error) => {
          this.error = error?.error?.message || 'Error updating order';
          this.cdr.detectChanges();
        }
      });
  }

  onCancel(): void {
    this.router.navigate(['/orders']);
  }
}

