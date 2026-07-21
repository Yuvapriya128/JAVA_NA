import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CustomerResponse, CustomerService } from '../../../services/customer/customer.service';
import { PageResponse } from '../../../services/shared/page-response';
import { TableAction, TableActionButtonsComponent } from '../../shared/buttons/table-action-buttons.component';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableToolbarComponent } from '../../shared/search/table-toolbar/table-toolbar.component';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { ConfirmationDialogService } from '../../../services/shared/confirmation-dialog.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { ToastNotificationService } from '../../../services/shared/toast-notification.service';

@Component({
  selector: 'app-customer-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    StatusBadgeComponent,
    PageHeaderComponent,
    PaginationComponent,
    TableToolbarComponent,
    TableActionButtonsComponent,
    EmptyStateComponent
  ],
  templateUrl: './customer-list.component.html',
  styleUrls: ['./customer-list.component.css'],
})
export class CustomerListComponent implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly router = inject(Router);
  private readonly confirmationDialog = inject(ConfirmationDialogService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly toastService = inject(ToastNotificationService);

  readonly customerRows = signal<CustomerResponse[]>([]);
  readonly allRows = signal<CustomerResponse[]>([]);
  readonly statsRows = signal<CustomerResponse[]>([]);
  readonly searchTerm = signal('');
  readonly roleFilter = signal('ALL');
  readonly statusFilter = signal('ALL');
  readonly approvalFilter = signal<'ALL' | 'PENDING' | 'ACTIVE'>('ALL');
  readonly cityFilter = signal('ALL');
  readonly sortBy = signal<'customerId' | 'customerName' | 'city' | 'creditScore'>('customerId');
  readonly sortDirection = signal<'asc' | 'desc'>('asc');
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');
  readonly page = signal(0);
  readonly size = signal(10);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);
  readonly pageSizeOptions = [10, 20, 50];

  readonly rowActions: TableAction[] = [
    { key: 'view', icon: 'bi-eye', className: 'btn btn-outline-primary', label: 'View', tooltip: 'View customer details' },
    { key: 'edit', icon: 'bi-pencil', className: 'btn btn-outline-secondary', label: 'Edit', tooltip: 'Edit customer details' },
    { key: 'deactivate', icon: 'bi-person-x', className: 'btn btn-outline-danger', label: 'Deactivate', tooltip: 'Deactivate customer' }
  ];
  readonly adminRowActions: TableAction[] = [
    { key: 'view', icon: 'bi-eye', className: 'btn btn-outline-primary', label: 'View', tooltip: 'View customer details' },
    { key: 'edit', icon: 'bi-pencil', className: 'btn btn-outline-secondary', label: 'Edit', tooltip: 'Edit customer details' }
  ];

  ngOnInit(): void {
    this.loadStats();
    this.loadData();
  }

  loadData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    const sortField = this.sortBy();
    const sortDir = this.sortDirection().toUpperCase();
    const activeFilter = this.resolveActiveFilter();

    if (activeFilter === 'CONFLICT') {
      this.allRows.set([]);
      this.customerRows.set([]);
      this.totalElements.set(0);
      this.totalPages.set(0);
      this.status.set({ loading: false, success: false, error: '' });
      return;
    }

    const request$ = activeFilter === undefined
      ? this.customerService.getAllCustomers(this.page(), this.size(), sortField, sortDir)
      : this.customerService.searchCustomers(
        undefined,
        undefined,
        undefined,
        undefined,
        undefined,
        activeFilter,
        undefined,
        undefined,
        this.page(),
        this.size(),
        sortField,
        sortDir
      );

    request$.subscribe({
      next: (response: PageResponse<CustomerResponse>) => {
        this.allRows.set(response.content);
        this.totalElements.set(response.totalElements);
        this.totalPages.set(response.totalPages);
        this.applyFilters();
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
        this.successMessage.set('');
      }
    });
  }

  onSearch(): void {
    this.page.set(0);
    this.loadData();
  }

  onFilterChange(): void {
    this.page.set(0);
    this.loadData();
  }

  setApprovalFilter(filter: 'ALL' | 'PENDING' | 'ACTIVE'): void {
    this.approvalFilter.set(filter);
    this.page.set(0);
    this.loadData();
  }

  onSortChange(sortBy: 'customerId' | 'customerName' | 'city' | 'creditScore'): void {
    if (this.sortBy() === sortBy) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortBy.set(sortBy);
      this.sortDirection.set('asc');
    }
    this.loadData();
  }

  onSortFieldChange(sortBy: 'customerId' | 'customerName' | 'city' | 'creditScore'): void {
    if (this.sortBy() !== sortBy) {
      this.sortBy.set(sortBy);
      this.page.set(0);
      this.loadData();
    }
  }

  onSortDirectionChange(direction: 'asc' | 'desc'): void {
    if (this.sortDirection() !== direction) {
      this.sortDirection.set(direction);
      this.page.set(0);
      this.loadData();
    }
  }

  onAction(action: string, customerId: number): void {
    if (action === 'view') {
      this.view(customerId);
      return;
    }

    if (action === 'deactivate') {
      this.delete(customerId);
      return;
    }
    if (action === 'edit') {
      this.edit(customerId);
    }
  }

  isAdminView(): boolean {
    return this.tokenStorage.hasRole(['ADMIN']);
  }

  canApprove(customer: CustomerResponse): boolean {
    return this.isAdminView() && !customer.active;
  }

  canReject(customer: CustomerResponse): boolean {
    return this.isAdminView() && customer.active;
  }

  approve(customer: CustomerResponse): void {
    if (!this.canApprove(customer)) {
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.customerService.approveCustomer(customer.customerId).subscribe({
      next: () => {
        this.status.set({ loading: false, success: true, error: '' });
        this.successMessage.set('Customer approved successfully.');
        this.toastService.show('Customer approved', 'success');
        this.refresh();
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to approve customer.'
        });
        this.successMessage.set('');
      }
    });
  }

  reject(customer: CustomerResponse): void {
    if (!this.canReject(customer)) {
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.customerService.deactivateCustomer(customer.customerId).subscribe({
      next: () => {
        this.status.set({ loading: false, success: true, error: '' });
        this.successMessage.set('Customer rejected successfully.');
        this.toastService.show('Customer rejected', 'warning');
        this.refresh();
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to reject customer.'
        });
        this.successMessage.set('');
      }
    });
  }

  view(customerId: number): void {
    this.router.navigate(['/customers/details'], { queryParams: { id: customerId } });
  }

  async delete(customerId: number): Promise<void> {
    const confirmed = await this.confirmationDialog.confirm({
      title: 'Deactivate Customer',
      message: 'Do you want to deactivate this customer account?',
      confirmText: 'Deactivate',
      cancelText: 'Keep Active',
      variant: 'danger'
    });
    if (!confirmed) {
      return;
    }
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.customerService.deactivateCustomer(customerId).subscribe({
      next: () => {
        this.status.set({ loading: false, success: true, error: '' });
        this.successMessage.set('Customer deactivated successfully.');
        this.refresh();
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
        this.successMessage.set('');
      }
    });
  }

  edit(customerId: number): void {
    this.router.navigate(['/customers/details'], { queryParams: { id: customerId, mode: 'edit' } });
  }

  openCustomer(customerId: number): void {
    this.view(customerId);
  }

  onPageChange(page: number): void {
    if (page === this.page()) {
      return;
    }
    this.page.set(page);
    this.loadData();
  }

  onPageSizeChange(size: number): void {
    this.size.set(size);
    this.page.set(0);
    this.loadData();
  }

  availableCities(): string[] {
    const values = this.allRows().map((item) => item.city).filter((city) => !!city);
    return Array.from(new Set(values)).sort((a, b) => a.localeCompare(b));
  }

  refresh(): void {
    this.loadStats();
    this.loadData();
  }

  initials(name: string): string {
    const parts = String(name || '').trim().split(/\s+/).filter(Boolean);
    if (parts.length === 0) {
      return 'NA';
    }
    if (parts.length === 1) {
      return parts[0].slice(0, 2).toUpperCase();
    }
    return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
  }

  totalCustomersCount(): number {
    return this.statsRows().length;
  }

  activeCustomersCount(): number {
    return this.statsRows().filter((customer) => customer.active).length;
  }

  managerCustomersCount(): number {
    return this.statsRows().filter((customer) => this.hasRoleValue(customer.role, 'MANAGER')).length;
  }

  adminCustomersCount(): number {
    return this.statsRows().filter((customer) => this.hasRoleValue(customer.role, 'ADMIN')).length;
  }

  roleLabel(role: string): string {
    return this.normalizeRole(role);
  }

  customerStatusLabel(customer: CustomerResponse): string {
    return customer.active ? 'Active' : 'Pending Approval';
  }

  private applyFilters(): void {
    const term = this.searchTerm().trim().toLowerCase();
    const role = this.roleFilter();
    const status = this.statusFilter();
    const approval = this.approvalFilter();
    const city = this.cityFilter();

    let filtered = this.allRows().filter((customer) => {
      const matchesTerm =
        !term ||
        customer.customerName.toLowerCase().includes(term) ||
        customer.email.toLowerCase().includes(term) ||
        customer.city.toLowerCase().includes(term) ||
        String(customer.customerId).includes(term);

      const matchesRole = role === 'ALL' || this.hasRoleValue(customer.role, role);
      const customerStatus = customer.active ? 'ACTIVE' : 'DEACTIVATED';
      const matchesStatus = status === 'ALL' || customerStatus === status;
      const matchesApproval =
        approval === 'ALL' ||
        (approval === 'ACTIVE' && customer.active) ||
        (approval === 'PENDING' && !customer.active);
      const matchesCity = city === 'ALL' || customer.city === city;
      return matchesTerm && matchesRole && matchesStatus && matchesApproval && matchesCity;
    });

    filtered = this.sortRows(filtered);
    this.customerRows.set(filtered);
  }

  private loadStats(): void {
    this.customerService.getAllCustomers(0, 5000, 'customerId', 'ASC').subscribe({
      next: (response: PageResponse<CustomerResponse>) => {
        this.statsRows.set(response.content);
      },
      error: () => {
        this.statsRows.set(this.allRows());
      }
    });
  }

  private hasRoleValue(role: string, expectedRole: 'USER' | 'MANAGER' | 'ADMIN' | string): boolean {
    return this.normalizeRole(role) === String(expectedRole || '').toUpperCase();
  }

  private normalizeRole(role: string): 'USER' | 'MANAGER' | 'ADMIN' | string {
    const raw = String(role || '').trim().toUpperCase();
    if (raw.startsWith('ROLE_')) {
      return raw.replace(/^ROLE_/, '');
    }
    return raw;
  }

  private sortRows(rows: CustomerResponse[]): CustomerResponse[] {
    const sorted = [...rows].sort((a, b) => {
      const key = this.sortBy();
      if (key === 'customerId' || key === 'creditScore') {
        return (a[key] as number) - (b[key] as number);
      }
      return String(a[key]).localeCompare(String(b[key]));
    });

    return this.sortDirection() === 'asc' ? sorted : sorted.reverse();
  }

  private resolveActiveFilter(): boolean | undefined | 'CONFLICT' {
    const fromApproval = this.approvalFilter() === 'ALL'
      ? undefined
      : this.approvalFilter() === 'ACTIVE';

    const fromStatus = this.statusFilter() === 'ALL'
      ? undefined
      : this.statusFilter() === 'ACTIVE';

    if (fromApproval !== undefined && fromStatus !== undefined && fromApproval !== fromStatus) {
      return 'CONFLICT';
    }

    return fromApproval !== undefined ? fromApproval : fromStatus;
  }
}
