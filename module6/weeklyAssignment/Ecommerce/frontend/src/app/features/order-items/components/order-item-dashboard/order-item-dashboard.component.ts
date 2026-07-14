import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { OrderItemService } from '../../../../core/services/order-item.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { CurrencyPipe } from '../../../../shared/pipes/currency.pipe';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { PERMISSIONS } from '../../../../core/auth/constants/permissions.constants';

@Component({
  selector: 'app-order-item-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    PaginationComponent,
    LoadingComponent,
  ],
  templateUrl: './order-item-dashboard.component.html',
  styleUrl: './order-item-dashboard.component.css'})
export class OrderItemDashboardComponent implements OnInit {
  allOrderItems: any[] = [];
  orderItems: any[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 1;
  pageSize = 10;

  private readonly authState = inject(AuthStateService);

  constructor(private orderItemService: OrderItemService, private changeDetectorRef: ChangeDetectorRef, private router: Router) {}

  ngOnInit(): void {
    this.loadOrderItems();
  }

  loadOrderItems(): void {
    this.isLoading = true;
    this.orderItemService.getAllOrderItems().subscribe({
      next: (response) => {
        this.allOrderItems = Array.isArray(response) ? response : [];
        this.totalPages = Math.max(1, Math.ceil(this.allOrderItems.length / this.pageSize));
        this.applyPageSlice();
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();  // ✓ Trigger change detection
      },
      error: (error) => {
        console.error('Error loading order items:', error);
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();  // ✓ Trigger change detection
      }
    });
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.applyPageSlice();
    this.changeDetectorRef.markForCheck();  // ✓ Trigger change detection
  }

  private applyPageSlice(): void {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.orderItems = this.allOrderItems.slice(start, end);
  }

  canCreateOrderItem(): boolean {
    return this.authState.hasPermission(PERMISSIONS.ORDER_ITEM_CREATE);
  }

  canEditOrderItem(): boolean {
    return this.authState.hasPermission(PERMISSIONS.ORDER_ITEM_UPDATE);
  }

  canDeleteOrderItem(): boolean {
    return this.authState.hasPermission(PERMISSIONS.ORDER_ITEM_DELETE);
  }

  onAddOrderItem(): void {
    if (!this.canCreateOrderItem()) {
      return;
    }
    console.log('[OrderItemDashboardComponent] Clicked Create');
    this.router.navigate(['/orderitems/create']);
  }

  onViewOrderItem(id: number): void {
    console.log('[OrderItemDashboardComponent] Clicked View', id);
    this.router.navigate(['/orderitems', id]);
  }

  onEditOrderItem(id: number): void {
    if (!this.canEditOrderItem()) {
      return;
    }
    console.log('[OrderItemDashboardComponent] Clicked Edit', id);
    this.router.navigate(['/orderitems/edit', id]);
  }

  onDeleteOrderItem(id: number): void {
    if (!this.canDeleteOrderItem()) {
      return;
    }
    console.log('[OrderItemDashboardComponent] Clicked Delete', id);
    if (confirm('Are you sure you want to delete this order item?')) {
      this.orderItemService.deleteOrderItem(id).subscribe({
        next: () => {
          console.log('✓ Order item deleted successfully');
          this.loadOrderItems();
        },
        error: (error) => {
          console.error('✗ Error deleting order item:', error);
          alert(`Failed to delete order item: ${error?.error?.message || 'Unknown error'}`);
        }
      });
    }
  }
}
