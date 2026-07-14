import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderItemService } from '../../../../core/services/order-item.service';
import { finalize, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-order-item-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-item-view.component.html',
  styleUrl: './order-item-view.component.css'})
export class OrderItemViewComponent implements OnInit, OnDestroy {
  item: any;
  isLoading = true;
  itemId: number = 0;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderItemService: OrderItemService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('[OrderItemViewComponent] View Component Loaded');
    this.route.paramMap
      .pipe(
        distinctUntilChanged((prev, curr) => prev.get('id') === curr.get('id')),
        takeUntil(this.destroy$)
      )
      .subscribe(params => {
        console.log('[OrderItemViewComponent] Route Params:', params);
        this.itemId = +params.get('id')!;
        console.log('[OrderItemViewComponent] Extracted ID:', this.itemId);
        this.loadOrderItem();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrderItem(): void {
    // Validate route parameter
    if (!this.itemId || this.itemId <= 0 || isNaN(this.itemId)) {
      console.warn('[OrderItemViewComponent] Invalid ID:', this.itemId);
      this.isLoading = false;
      this.errorMessage = 'Invalid order item ID';
      this.cdr.detectChanges();
      return;
    }

    console.log('[OrderItemViewComponent] Calling getById:', this.itemId);
    this.orderItemService.getOrderItemById(this.itemId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          console.log('[OrderItemViewComponent] API Response:', data);
          this.item = data;
          this.errorMessage = '';
          console.log('[OrderItemViewComponent] Assigned Object:', this.item);
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[OrderItemViewComponent] API Error:', error);
          this.item = null;

          if (error.status === 404) {
            this.errorMessage = 'Order item not found';
          } else if (error.status === 403) {
            this.errorMessage = 'Access denied';
          } else {
            this.errorMessage = 'Error loading order item: ' + (error?.error?.message || error?.message || 'Unknown error');
          }
          this.cdr.detectChanges();
        }
      });
  }

  onEdit(): void {
    this.router.navigate(['/orderitems/edit', this.itemId]);
  }

  onDelete(): void {
    if (confirm('Are you sure?')) {
      this.orderItemService.deleteOrderItem(this.itemId).subscribe({
        next: () => {
          this.router.navigate(['/orderitems']);
        },
        error: (error) => alert('Delete failed: ' + error?.error?.message)
      });
    }
  }

  onBack(): void {
    this.router.navigate(['/orderitems']);
  }
}
