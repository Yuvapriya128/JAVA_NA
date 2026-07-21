import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth/auth.service';
import { LoanService, LoanSummary } from '../../../services/loan/loan.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { TableAction, TableActionButtonsComponent } from '../../shared/buttons/table-action-buttons.component';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableToolbarComponent } from '../../shared/search/table-toolbar/table-toolbar.component';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';
import { ConfirmationDialogService } from '../../../services/shared/confirmation-dialog.service';

@Component({
  selector: 'app-loan-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    StatusBadgeComponent,
    PageHeaderComponent,
    PaginationComponent,
    TableToolbarComponent,
    TableActionButtonsComponent,
    EmptyStateComponent
  ],
  templateUrl: './loan-list.component.html',
  styleUrls: ['./loan-list.component.css'],
})
export class LoanListComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);
  private readonly confirmationDialog = inject(ConfirmationDialogService);
  private userCustomerId: number | null = null;

  readonly loanRows = signal<LoanSummary[]>([]);
  readonly allLoanRows = signal<LoanSummary[]>([]);
  readonly searchTerm = signal('');
  readonly searchField = signal<'all' | 'id' | 'customer' | 'city'>('all');
  readonly loanTypeFilter = signal('ALL');
  readonly statusFilter = signal('ALL');
  readonly interestFilter = signal<'ALL' | 'LOW' | 'MEDIUM' | 'HIGH'>('ALL');
  readonly sortBy = signal<'loanId' | 'loanType' | 'annualInterestRate' | 'principalAmount' | 'emiAmount'>('loanId');
  readonly sortDirection = signal<'asc' | 'desc'>('asc');
  readonly page = signal(0);
  readonly size = signal(10);
  readonly totalPages = signal(0);
  readonly totalElements = signal(0);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');
  readonly pageSizeOptions = [10, 20, 50];

  private readonly managerActions: TableAction[] = [
    { key: 'view', icon: 'bi-eye', className: 'btn btn-outline-primary', label: 'View', tooltip: 'View loan details' },
    { key: 'edit', icon: 'bi-pencil', className: 'btn btn-outline-secondary', label: 'Edit', tooltip: 'Edit loan' },
    { key: 'delete', icon: 'bi-trash', className: 'btn btn-outline-danger', label: 'Delete', tooltip: 'Delete loan' }
  ];

  private readonly userActions: TableAction[] = [
    { key: 'view', icon: 'bi-eye', className: 'btn btn-outline-primary', label: 'View', tooltip: 'View loan details' }
  ];

  ngOnInit(): void {
    this.initializeUserContextAndLoad();
  }

  private initializeUserContextAndLoad(): void {
    if (this.canManageLoans()) {
      this.loadData();
      return;
    }

    const cachedUser = this.authService.getCurrentUserSync();
    if (cachedUser?.customerId) {
      this.userCustomerId = cachedUser.customerId;
      this.loadData();
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.userCustomerId = user.customerId;
        this.loadData();
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to resolve current user profile.'
        });
        this.successMessage.set('');
      }
    });
  }

  loadData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.getLoans(0, 200).subscribe({
      next: (response) => {
        const scopedRows = this.canManageLoans()
          ? response.content
          : response.content.filter((row) => this.userCustomerId !== null && row.customerId === this.userCustomerId);

        this.allLoanRows.set(scopedRows);
        this.applyFilters();
        this.status.set({
          loading: false,
          success: !this.canManageLoans() && scopedRows.length === 0,
          error: ''
        });
        this.successMessage.set(!this.canManageLoans() && scopedRows.length === 0 ? 'No loans found for your account yet.' : '');
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
    this.applyFilters();
  }

  onSearchFieldChange(field: 'all' | 'id' | 'customer' | 'city'): void {
    this.searchField.set(field);
    this.onSearch();
  }

  onPageChange(page: number): void {
    if (page === this.page()) {
      return;
    }
    this.page.set(page);
    this.applyFilters();
  }

  onPageSizeChange(size: number): void {
    this.size.set(size);
    this.page.set(0);
    this.applyFilters();
  }

  onFilterChange(): void {
    this.page.set(0);
    this.applyFilters();
  }

  onSortChange(sortBy: 'loanId' | 'loanType' | 'annualInterestRate' | 'principalAmount' | 'emiAmount'): void {
    if (this.sortBy() === sortBy) {
      this.sortDirection.set(this.sortDirection() === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortBy.set(sortBy);
      this.sortDirection.set('asc');
    }
    this.applyFilters();
  }

  onSortFieldChange(sortBy: 'loanId' | 'loanType' | 'annualInterestRate' | 'principalAmount' | 'emiAmount'): void {
    if (this.sortBy() !== sortBy) {
      this.sortBy.set(sortBy);
      this.page.set(0);
      this.applyFilters();
    }
  }

  onSortDirectionChange(direction: 'asc' | 'desc'): void {
    if (this.sortDirection() !== direction) {
      this.sortDirection.set(direction);
      this.page.set(0);
      this.applyFilters();
    }
  }

  canManageLoans(): boolean {
    return this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  availableActions(): TableAction[] {
    return this.canManageLoans() ? this.managerActions : this.userActions;
  }

  onAction(action: string, loanId: number): void {
    if (action === 'view') {
      this.view(loanId);
      return;
    }

    if (action === 'delete') {
      this.delete(loanId);
      return;
    }
    if (action === 'edit') {
      this.edit(loanId);
    }
  }

  view(loanId: number): void {
    this.router.navigate(['/loans/details'], { queryParams: { id: loanId } });
  }

  async delete(loanId: number): Promise<void> {
    const confirmed = await this.confirmationDialog.confirm({
      title: 'Delete Loan',
      message: 'This action will permanently remove this loan record. Continue?',
      confirmText: 'Delete',
      cancelText: 'Cancel',
      variant: 'danger'
    });
    if (!confirmed) {
      return;
    }
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.deleteLoan(loanId).subscribe({
      next: () => {
        const nextRows = this.allLoanRows().filter((row) => row.loanId !== loanId);
        this.allLoanRows.set(nextRows);
        this.applyFilters();
        this.status.set({ loading: false, success: true, error: '' });
        this.successMessage.set('Loan deleted successfully.');
        this.resyncAfterDelete(loanId);
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

  edit(loanId: number): void {
    this.router.navigate(['/loans/details'], { queryParams: { id: loanId, mode: 'edit' } });
  }

  openLoan(loanId: number): void {
    this.view(loanId);
  }

  availableLoanTypes(): string[] {
    const values = this.allLoanRows().map((item) => item.loanType).filter((loanType) => !!loanType);
    return Array.from(new Set(values)).sort((a, b) => a.localeCompare(b));
  }

  refresh(): void {
    this.loadData();
  }

  private resyncAfterDelete(loanId: number): void {
    this.loanService.getLoans(0, 200).subscribe({
      next: (response) => {
        const scopedRows = this.canManageLoans()
          ? response.content
          : response.content.filter((row) => this.userCustomerId !== null && row.customerId === this.userCustomerId);

        this.allLoanRows.set(scopedRows);
        this.applyFilters();

        const stillExists = scopedRows.some((item) => item.loanId === loanId);
        if (stillExists) {
          this.status.set({
            loading: false,
            success: false,
            error: `Delete was acknowledged, but Loan #${loanId} still exists on server. Please verify backend delete behavior.`
          });
          this.successMessage.set('');
        }
      },
      error: () => {
        // Keep optimistic UI state if refresh fails.
      }
    });
  }

  private applyFilters(): void {
    const term = this.searchTerm().trim().toLowerCase();

    let filtered = this.allLoanRows().filter((loan) => {
      const matchesTerm = !term || this.matchesSearch(loan, term);
      const matchesType = this.loanTypeFilter() === 'ALL' || loan.loanType === this.loanTypeFilter();
      const matchesStatus = this.statusFilter() === 'ALL' || loan.loanStatus === this.statusFilter();
      const interestRate = this.toNumber(loan.annualInterestRate);
      const matchesInterest = this.matchesInterestFilter(interestRate);
      return matchesTerm && matchesType && matchesStatus && matchesInterest;
    });

    filtered = this.sortRows(filtered);
    this.totalElements.set(filtered.length);
    const totalPages = Math.max(1, Math.ceil(filtered.length / this.size()));
    this.totalPages.set(totalPages);

    const currentPage = Math.min(this.page(), totalPages - 1);
    this.page.set(currentPage);

    const start = currentPage * this.size();
    this.loanRows.set(filtered.slice(start, start + this.size()));
  }

  private sortRows(rows: LoanSummary[]): LoanSummary[] {
    const sorted = [...rows].sort((a, b) => {
      const sortKey = this.sortBy();
      if (sortKey === 'loanType') {
        return a.loanType.localeCompare(b.loanType);
      }

      return this.toNumber(a[sortKey]) - this.toNumber(b[sortKey]);
    });

    return this.sortDirection() === 'asc' ? sorted : sorted.reverse();
  }

  private matchesInterestFilter(interestRate: number): boolean {
    const filter = this.interestFilter();
    if (filter === 'ALL') {
      return true;
    }
    if (filter === 'LOW') {
      return interestRate < 9;
    }
    if (filter === 'MEDIUM') {
      return interestRate >= 9 && interestRate <= 12;
    }
    return interestRate > 12;
  }

  private toNumber(value: number | string): number {
    if (typeof value === 'number') {
      return value;
    }
    const parsed = Number(String(value).replace(/[^\d.-]/g, ''));
    return Number.isFinite(parsed) ? parsed : 0;
  }

  private matchesSearch(loan: LoanSummary, term: string): boolean {
    const byField = this.searchField();

    if (byField === 'id') {
      return String(loan.loanId).includes(term);
    }

    if (byField === 'customer') {
      return loan.customerName.toLowerCase().includes(term);
    }

    if (byField === 'city') {
      return loan.city.toLowerCase().includes(term);
    }

    return (
      String(loan.loanId).includes(term) ||
      loan.loanType.toLowerCase().includes(term) ||
      loan.customerName.toLowerCase().includes(term) ||
      loan.city.toLowerCase().includes(term)
    );
  }
}
