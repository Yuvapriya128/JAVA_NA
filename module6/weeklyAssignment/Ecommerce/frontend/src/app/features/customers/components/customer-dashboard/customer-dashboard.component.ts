import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CustomerService } from '../../../../core/services/customer.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { SearchComponent } from '../../../../shared/components/search/search.component';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { PERMISSIONS } from '../../../../core/auth/constants/permissions.constants';

@Component({
  selector: 'app-customer-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    PaginationComponent,
    LoadingComponent,
    SearchComponent
  ],
  templateUrl: './customer-dashboard.component.html',
  styleUrl: './customer-dashboard.component.css'})
export class CustomerDashboardComponent implements OnInit {
  allCustomers: any[] = [];
  filteredCustomers: any[] = [];
  customers: any[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 1;
  pageSize = 10;

  private readonly authState = inject(AuthStateService);

  constructor(private customerService: CustomerService, private changeDetectorRef: ChangeDetectorRef, private router: Router) {}

  ngOnInit(): void {
    this.loadCustomers();
  }

  loadCustomers(): void {
    this.isLoading = true;
    this.customerService.getAllCustomers().subscribe({
      next: (response: any) => {
        this.allCustomers = (Array.isArray(response) ? response : []).map(customer => this.normalizeCustomer(customer));
        this.filteredCustomers = this.allCustomers;
        this.totalPages = Math.max(1, Math.ceil(this.filteredCustomers.length / this.pageSize));
        this.applyPageSlice(this.filteredCustomers);
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();  // ✓ Trigger change detection
      },
      error: (error: any) => {
        console.error('Error loading customers:', error);
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();  // ✓ Trigger change detection
      }
    });
  }

  onSearch(query: string): void {
    const normalized = query.trim().toLowerCase();
    this.filteredCustomers = !normalized
      ? this.allCustomers
      : this.allCustomers.filter(customer =>
            `${customer.name || ''}`.toLowerCase().includes(normalized)
            || `${customer.email || ''}`.toLowerCase().includes(normalized)
            || `${customer.address || ''}`.toLowerCase().includes(normalized)
        );

    this.currentPage = 0;
    this.totalPages = Math.max(1, Math.ceil(this.filteredCustomers.length / this.pageSize));
    this.applyPageSlice(this.filteredCustomers);
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.applyPageSlice(this.filteredCustomers);
  }

  private applyPageSlice(source: any[]): void {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.customers = source.slice(start, end);
  }

  private normalizeCustomer(customer: any): any {
    const resolvedId = this.resolveCustomerId(customer);
    return {
      ...customer,
      id: resolvedId
    };
  }

  private resolveCustomerId(customer: any): number | null {
    const rawId = customer?.id ?? customer?.customerId ?? customer?.customer_id;
    const id = Number(rawId);
    return Number.isFinite(id) && id > 0 ? id : null;
  }

   onAddCustomer(): void {
    if (!this.canCreateCustomer()) {
      return;
    }
    console.log('[CustomerDashboardComponent] Clicked Create');
    this.router.navigate(['/customers/create']);
  }

  onViewCustomer(customer: any): void {
    const id = this.resolveCustomerId(customer);
    console.log('[CustomerDashboardComponent] Clicked View', id, customer);

    if (!id) {
      alert('Cannot open customer: missing customer id in API response.');
      return;
    }

    this.router.navigate(['/customers', id]);
  }

  onEditCustomer(customer: any): void {
    if (!this.canEditCustomer()) {
      return;
    }
    const id = this.resolveCustomerId(customer);
    console.log('[CustomerDashboardComponent] Clicked Edit', id, customer);

    if (!id) {
      alert('Cannot edit customer: missing customer id in API response.');
      return;
    }

    this.router.navigate(['/customers/edit', id]);
  }

  onDeleteCustomer(customer: any): void {
    if (!this.canDeleteCustomer()) {
      return;
    }
    const id = this.resolveCustomerId(customer);
    console.log('[CustomerDashboardComponent] Clicked Delete', id, customer);

    if (!id) {
      alert('Cannot delete customer: missing customer id in API response.');
      return;
    }

    if (confirm('Are you sure you want to delete this customer?')) {
      this.customerService.deleteCustomer(id).subscribe({
        next: () => {
          console.log('✓ Customer deleted successfully');
          this.loadCustomers();
        },
        error: (error: any) => {
          console.error('✗ Error deleting customer:', error);
          alert(`Failed to delete customer: ${error?.error?.message || 'Unknown error'}`);
        }
      });
    }
  }

  canCreateCustomer(): boolean {
    return this.authState.hasPermission(PERMISSIONS.CUSTOMER_CREATE);
  }

  canEditCustomer(): boolean {
    return this.authState.hasPermission(PERMISSIONS.CUSTOMER_UPDATE);
  }

  canDeleteCustomer(): boolean {
    return this.authState.hasPermission(PERMISSIONS.CUSTOMER_DELETE);
  }
}
