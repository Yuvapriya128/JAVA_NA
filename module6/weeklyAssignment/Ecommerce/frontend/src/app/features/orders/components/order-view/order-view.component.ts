import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { OrderService } from '../../../../core/services/order.service';
import { OrderItemService } from '../../../../core/services/order-item.service';
import { finalize, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { PERMISSIONS } from '../../../../core/auth/constants/permissions.constants';

@Component({
  selector: 'app-order-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './order-view.component.html',
  styleUrl: './order-view.component.css'})
export class OrderViewComponent implements OnInit, OnDestroy {
  order: any;
  orderItems: any[] = [];
  isLoading = true;
  orderId: number = 0;
  errorMessage = '';
  private destroy$ = new Subject<void>();
  private readonly authState = inject(AuthStateService);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private orderService: OrderService,
    private orderItemService: OrderItemService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('[OrderViewComponent] View Component Loaded');
    this.route.paramMap.pipe(
      distinctUntilChanged((prev, curr) => prev.get('id') === curr.get('id')),
      takeUntil(this.destroy$)
    ).subscribe(params => {
      console.log('[OrderViewComponent] Route Params:', params);
      this.orderId = +params.get('id')!;
      console.log('[OrderViewComponent] Extracted ID:', this.orderId);
      this.loadOrder();
    });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadOrder(): void {
    // Validate route parameter
    if (!this.orderId || this.orderId <= 0 || isNaN(this.orderId)) {
      console.warn('[OrderViewComponent] Invalid ID:', this.orderId);
      this.isLoading = false;
      this.errorMessage = 'Invalid order ID';
      this.cdr.detectChanges();
      return;
    }

    console.log('[OrderViewComponent] Calling getById:', this.orderId);
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
          console.log('[OrderViewComponent] API Response:', data);
          this.order = data;
          this.errorMessage = '';
          console.log('[OrderViewComponent] Assigned Object:', this.order);
          this.loadOrderItems(this.orderId);
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[OrderViewComponent] API Error:', error);
          this.order = null;

          if (error.status === 404) {
            this.errorMessage = 'Order not found';
          } else if (error.status === 403) {
            this.errorMessage = 'Access denied';
          } else {
            this.errorMessage = 'Error loading order: ' + (error?.error?.message || error?.message || 'Unknown error');
          }
          this.cdr.detectChanges();
        }
      });
  }

  private loadOrderItems(orderId: number): void {
    console.log('[OrderViewComponent] Loading items for order:', orderId);
    this.orderItemService.getOrderItemsByOrder(orderId)
      .pipe(
        takeUntil(this.destroy$)
      )
      .subscribe({
        next: (items) => {
          console.log('[OrderViewComponent] Order items loaded:', items);
          this.orderItems = items || [];
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[OrderViewComponent] Error loading items:', error);
          this.orderItems = [];
        }
      });
  }

  onEdit(): void {
    if (!this.canEditOrder()) {
      return;
    }
    this.router.navigate(['/orders/edit', this.orderId]);
  }

  onDelete(): void {
    if (!this.canDeleteOrder()) {
      return;
    }

    if (confirm(this.isUserRole() ? 'Are you sure you want to cancel this order?' : 'Are you sure?')) {
      this.orderService.deleteOrder(this.orderId).subscribe({
        next: () => {
          this.router.navigate(['/orders']);
        },
        error: (error) => alert('Delete failed: ' + error?.error?.message)
      });
    }
  }

  canEditOrder(): boolean {
    if (this.isUserRole()) {
      return false;
    }

    return this.authState.hasPermission(PERMISSIONS.ORDER_UPDATE);
  }

  canDeleteOrder(): boolean {
    if (!this.order) {
      return false;
    }

    if (this.isUserRole()) {
      return Number(this.order.customerId) === this.getCurrentUserId()
        && this.authState.hasPermission(PERMISSIONS.ORDER_CANCEL_OWN);
    }

    return this.authState.hasPermission(PERMISSIONS.ORDER_DELETE);
  }

  isUserRole(): boolean {
    return this.authState.isUser();
  }

  private getCurrentUserId(): number | null {
    const user = this.authState.currentUser();
    const id = Number(user?.customerId);
    return Number.isFinite(id) && id > 0 ? id : null;
  }

  onBack(): void {
    this.router.navigate(['/orders']);
  }
}
