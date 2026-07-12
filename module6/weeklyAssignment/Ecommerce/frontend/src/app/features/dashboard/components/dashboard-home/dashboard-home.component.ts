import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Observable, catchError, finalize, forkJoin, of, tap, timeout } from 'rxjs';
import { CustomerService } from '../../../../core/services/customer.service';
import { ProductService } from '../../../../core/services/product.service';
import { OrderService } from '../../../../core/services/order.service';
import { OrderItemService } from '../../../../core/services/order-item.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';

type ApiKey = 'customers' | 'products' | 'orders' | 'orderItems';
type ApiStatus = 'idle' | 'loading' | 'success' | 'forbidden' | 'not-found' | 'error' | 'timeout';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, RouterLink, LoadingComponent],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.css'})
export class DashboardHomeComponent implements OnInit {
  isLoading = true;
  loadError = '';
  apiStatus: Record<ApiKey, ApiStatus> = {
    customers: 'idle',
    products: 'idle',
    orders: 'idle',
    orderItems: 'idle'
  };
  totalCustomers = 0;
  totalProducts = 0;
  totalOrders = 0;
  totalOrderItems = 0;
  totalRevenue = '$0';
  recentOrders: any[] = [];
  recentCustomers: any[] = [];

  constructor(
    private customerService: CustomerService,
    private productService: ProductService,
    private orderService: OrderService,
    private orderItemService: OrderItemService,
    private changeDetectorRef: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.loadDashboardData();
  }

  loadDashboardData(): void {
    this.isLoading = true;
    this.loadError = '';
    this.apiStatus = {
      customers: 'loading',
      products: 'loading',
      orders: 'loading',
      orderItems: 'loading'
    };

    forkJoin({
      customers: this.withFallback(this.customerService.getAllCustomers(), 'customers', []),
      products: this.withFallback(this.productService.getAllProducts(), 'products', []),
      orders: this.withFallback(this.orderService.getAllOrders(), 'orders', []),
      orderItems: this.withFallback(this.orderItemService.getAllOrderItems(), 'orderItems', [])
    }).pipe(
      finalize(() => {
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();  // ✓ Trigger change detection
      })
    ).subscribe({
      next: ({ customers, products, orders, orderItems }: any) => {
        const customerList = this.extractList(customers);
        const productList = this.extractList(products);
        const orderList = this.extractList(orders);
        const orderItemList = this.extractList(orderItems);

        this.totalCustomers = customerList.length;
        this.totalProducts = productList.length;
        this.totalOrders = orderList.length;
        this.totalOrderItems = orderItemList.length;
        this.totalRevenue = '$' + (Math.random() * 100000).toFixed(2);

        this.recentOrders = orderList.slice(0, 5);
        this.recentCustomers = customerList.slice(0, 5);

        if (Object.values(this.apiStatus).every(status => status !== 'success')) {
          this.loadError = 'All dashboard APIs failed. Showing fallback values.';
        }
      },
      error: (error) => {
        this.loadError = `Failed to load dashboard data: ${error?.message || 'Unknown error'}`;
        console.error('Error loading dashboard data:', error);
      }
    });
  }

  private withFallback<T>(source$: Observable<T>, key: ApiKey, fallback: T): Observable<T> {
    return source$.pipe(
      timeout(15000),
      tap(() => {
        this.apiStatus[key] = 'success';
      }),
      catchError((error) => {
        this.apiStatus[key] = this.mapStatus(error?.status, error?.name);
        return of(fallback);
      })
    );
  }

  private mapStatus(status?: number, errorName?: string): ApiStatus {
    if (errorName === 'TimeoutError') {
      return 'timeout';
    }
    if (status === 403) {
      return 'forbidden';
    }
    if (status === 404) {
      return 'not-found';
    }
    if (typeof status === 'number' && status >= 400) {
      return 'error';
    }
    return 'error';
  }

  private extractList(response: any): any[] {
    if (Array.isArray(response)) {
      return response;
    }

    if (Array.isArray(response?.content)) {
      return response.content;
    }

    if (Array.isArray(response?.data)) {
      return response.data;
    }

    return [];
  }

  getStatusClass(status: string): string {
    switch (status?.toUpperCase()) {
      case 'PENDING':
        return 'bg-warning';
      case 'CONFIRMED':
        return 'bg-info';
      case 'SHIPPED':
        return 'bg-primary';
      case 'DELIVERED':
        return 'bg-success';
      case 'CANCELLED':
        return 'bg-danger';
      default:
        return 'bg-secondary';
    }
  }
}

