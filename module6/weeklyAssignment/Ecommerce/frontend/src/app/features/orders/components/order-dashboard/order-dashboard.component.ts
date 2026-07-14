import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subscription, forkJoin, of } from 'rxjs';
import { catchError, finalize, map, switchMap } from 'rxjs/operators';
import { OrderService } from '../../../../core/services/order.service';
import { OrderItemService } from '../../../../core/services/order-item.service';
import { OrderRefreshService } from '../../../../core/services/order-refresh.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { DateFormatPipe } from '../../../../shared/pipes/date-format.pipe';
import { CurrencyPipe } from '../../../../shared/pipes/currency.pipe';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { PERMISSIONS } from '../../../../core/auth/constants/permissions.constants';

@Component({
  selector: 'app-order-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    PaginationComponent,
    LoadingComponent,
    DateFormatPipe,
    CurrencyPipe
  ],
  templateUrl: './order-dashboard.component.html',
  styleUrl: './order-dashboard.component.css'})
export class OrderDashboardComponent implements OnInit {
  allOrders: any[] = [];
  orders: any[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 1;
  pageSize = 10;
  private readonly authState = inject(AuthStateService);
  private refreshSubscription?: Subscription;

  constructor(
    private orderService: OrderService,
    private orderItemService: OrderItemService,
    private orderRefreshService: OrderRefreshService,
    private changeDetectorRef: ChangeDetectorRef,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadOrders();
    this.refreshSubscription = this.orderRefreshService.refresh$.subscribe(() => {
      this.currentPage = 0;
      this.loadOrders();
    });
  }

  ngOnDestroy(): void {
    this.refreshSubscription?.unsubscribe();
  }

  loadOrders(): void {
    this.isLoading = true;

    const request$ = this.isUserRole()
      ? this.orderService.getOrdersByCustomer(this.getCurrentUserId() ?? -1)
      : this.orderService.getAllOrders();

    request$
      .pipe(
        map((response: any) => (Array.isArray(response) ? response : []).sort((a, b) => Number(b.id) - Number(a.id))),
        switchMap((orders) => this.enrichOrdersWithItemCount(orders)),
        finalize(() => {
          this.isLoading = false;
          this.changeDetectorRef.markForCheck();
        })
      )
      .subscribe({
      next: (ordersWithItems: any[]) => {
        this.allOrders = ordersWithItems;
        this.totalPages = Math.max(1, Math.ceil(this.allOrders.length / this.pageSize));
        this.applyPageSlice();
      },
      error: (error: any) => {
        console.error('Error loading orders:', error);
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.applyPageSlice();
  }

  private applyPageSlice(): void {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.orders = this.allOrders.slice(start, end);
  }

   onAddOrder(): void {
    if (!this.canCreateOrder()) {
      return;
    }
    console.log('[OrderDashboardComponent] Clicked Create');
    this.router.navigate(['/orders/create']);
  }

  onViewOrder(id: number): void {
    console.log('[OrderDashboardComponent] Clicked View', id);
    this.router.navigate(['/orders', id]);
  }

  onEditOrder(id: number): void {
    const order = this.allOrders.find((item) => Number(item.id) === Number(id));
    if (!this.canEditOrder(order)) {
      return;
    }
    console.log('[OrderDashboardComponent] Clicked Edit', id);
    this.router.navigate(['/orders/edit', id]);
  }

  onDeleteOrder(id: number): void {
    const order = this.allOrders.find((item) => Number(item.id) === Number(id));
    if (!this.canDeleteOrder(order)) {
      return;
    }

    console.log('[OrderDashboardComponent] Clicked Delete', id);
    if (confirm(this.isUserRole() ? 'Are you sure you want to cancel this order?' : 'Are you sure you want to delete this order?')) {
      this.orderService.deleteOrder(id).subscribe({
        next: () => {
          console.log('✓ Order deleted successfully');
          this.loadOrders();
        },
        error: (error: any) => {
          console.error('✗ Error deleting order:', error);
          alert(`Failed to delete order: ${error?.error?.message || 'Unknown error'}`);
        }
      });
    }
  }

  canCreateOrder(): boolean {
    return this.authState.hasPermission(PERMISSIONS.ORDER_CREATE);
  }

  canEditOrder(order: any): boolean {
    if (!order) {
      return false;
    }

    if (this.isUserRole()) {
      return false;
    }

    return this.authState.hasPermission(PERMISSIONS.ORDER_UPDATE);
  }

  canDeleteOrder(order: any): boolean {
    if (!order) {
      return false;
    }

    if (this.isUserRole()) {
      return Number(order.customerId) === this.getCurrentUserId()
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

  private enrichOrdersWithItemCount(orders: any[]) {
    if (!orders.length) {
      return of([]);
    }

    return forkJoin(
      orders.map((order) =>
        this.orderItemService.getOrderItemsByOrder(Number(order.id)).pipe(
          map((items) => {
            const itemCount = Array.isArray(items)
              ? items.reduce((sum, item) => sum + Number(item?.quantity || 0), 0)
              : 0;
            return { ...order, itemCount };
          }),
          catchError(() => of({ ...order, itemCount: 0 }))
        )
      )
    );
  }

  getStatusBadgeClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'CONFIRMED': 'badge bg-info',
      'PROCESSING': 'badge bg-warning text-dark',
      'SHIPPED': 'badge bg-primary',
      'DELIVERED': 'badge bg-success',
      'CANCELLED': 'badge bg-danger'
    };
    return statusMap[status] || 'badge bg-secondary';
  }

  getPaymentStatusBadgeClass(status: string): string {
    const statusMap: { [key: string]: string } = {
      'SUCCESS': 'badge bg-success',
      'PENDING': 'badge bg-warning text-dark',
      'FAILED': 'badge bg-danger'
    };
    return statusMap[status] || 'badge bg-secondary';
  }
}
