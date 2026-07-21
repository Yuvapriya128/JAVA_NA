import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { EmiPaymentHistory, LoanService, EmiPaymentReceiptDTO } from '../../../services/loan/loan.service';
import { PageResponse } from '../../../services/shared/page-response';
import { ToastNotificationService } from '../../../services/shared/toast-notification.service';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { TableToolbarComponent } from '../../shared/search/table-toolbar/table-toolbar.component';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { EmptyStateComponent } from '../../shared/empty-state/empty-state.component';

@Component({
  selector: 'app-payment-history',
  standalone: true,
  imports: [CommonModule, FormsModule, StatusBadgeComponent, PageHeaderComponent, TableToolbarComponent, PaginationComponent, EmptyStateComponent],
  templateUrl: './payment-history.component.html',
  styleUrls: ['./payment-history.component.css'],
})
export class PaymentHistoryComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly loanService = inject(LoanService);
  private readonly toastService = inject(ToastNotificationService);
  private currentUserCustomerId: number | null = null;
  private currentUserName = '';
  private currentUserEmail = '';

  readonly rows = signal<EmiPaymentHistory[]>([]);
  readonly allRows = signal<EmiPaymentHistory[]>([]);
  readonly selectedReceipt = signal<EmiPaymentHistory | null>(null);
  readonly receiptDetails = signal<EmiPaymentReceiptDTO | null>(null);
  readonly searchTerm = signal('');
  readonly selectedDate = signal('');
  readonly selectedMode = signal('ALL');
  readonly selectedStatus = signal('ALL');
  readonly selectedLoanId = signal('ALL');
  readonly selectedYear = signal('ALL');
  readonly customerTerm = signal('');
  readonly availableLoanIds = signal<number[]>([]);
  readonly availableYears = signal<string[]>([]);
  readonly totalPaidAmount = signal(0);
  readonly totalInterestPaid = signal(0);
  readonly totalPrincipalPaid = signal(0);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly receiptLoading = signal(false);
  readonly showReceiptModal = signal(false);
  readonly page = signal(0);
  readonly size = signal(10);
  readonly pageSizeOptions = [10, 20, 50];
  readonly totalPages = signal(1);
  readonly totalElements = signal(0);

  ngOnInit(): void {
    this.initializeUserContextAndLoad();
  }

  private initializeUserContextAndLoad(): void {
    if (this.canViewAllPayments()) {
      this.loadData();
      return;
    }

    const cachedUser = this.authService.getCurrentUserSync();
    if (cachedUser) {
      this.currentUserCustomerId = cachedUser.customerId;
      this.currentUserName = String(cachedUser.customerName || '').trim().toLowerCase();
      this.currentUserEmail = String(cachedUser.email || '').trim().toLowerCase();
      this.loadData();
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.authService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUserCustomerId = user.customerId;
        this.currentUserName = String(user.customerName || '').trim().toLowerCase();
        this.currentUserEmail = String(user.email || '').trim().toLowerCase();
        this.loadData();
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to resolve current user profile.'
        });
      }
    });
  }

  private canViewAllPayments(): boolean {
    return this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  showCustomerFilter(): boolean {
    return this.canViewAllPayments();
  }

  loadData(page = this.page()): void {
    this.status.set({ loading: true, success: false, error: '' });

    this.loanService.getEmiPayments(page, this.size()).subscribe({
      next: (response) => {
        const items = this.scopeRowsByRole(this.extractItems(response));
        this.allRows.set(items);
        this.availableLoanIds.set(this.extractLoanIds(items));
        this.availableYears.set(this.extractYears(items));
        this.applyFilters();
        this.selectedReceipt.set(null);
        this.receiptDetails.set(null);
        this.showReceiptModal.set(false);
        this.page.set(page);

        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
      }
    });
  }

  onSearch(): void {
    this.page.set(0);
    this.applyFilters();
  }

  onFilterChange(): void {
    this.page.set(0);
    this.applyFilters();
  }

  viewReceipt(row: EmiPaymentHistory): void {
    this.selectedReceipt.set(row);
    this.showReceiptModal.set(true);
    this.loadReceiptDetails(row.emiId);
  }

  closeReceiptModal(): void {
    this.showReceiptModal.set(false);
  }

  private loadReceiptDetails(emiId: number): void {
    this.receiptLoading.set(true);
    this.loanService.getPaymentReceipt(emiId).subscribe({
      next: (receipt) => {
        this.receiptDetails.set(receipt);
        this.receiptLoading.set(false);
      },
      error: (error) => {
        console.error('Failed to load receipt details:', error);
        this.toastService.show('Failed to load receipt details', 'danger');
        this.receiptLoading.set(false);
      }
    });
  }

  downloadReceipt(row?: EmiPaymentHistory): void {
    if (row) {
      const activeReceipt = this.receiptDetails();
      if (activeReceipt && activeReceipt.emiId === row.emiId) {
        this.triggerReceiptDownload(activeReceipt);
        return;
      }

      this.loanService.getPaymentReceipt(row.emiId).subscribe({
        next: (receipt) => {
          this.receiptDetails.set(receipt);
          this.triggerReceiptDownload(receipt);
        },
        error: () => {
          this.toastService.show('Failed to download receipt', 'danger');
        }
      });
      return;
    }

    const receipt = this.receiptDetails();
    if (!receipt) {
      this.toastService.show('No receipt to download', 'warning');
      return;
    }

    this.triggerReceiptDownload(receipt);
  }

  private triggerReceiptDownload(receipt: EmiPaymentReceiptDTO): void {

    const docString = this.generateReceiptDocument(receipt);
    const blob = new Blob([docString], { type: 'text/plain' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `${receipt.receiptNumber}.txt`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  exportCsv(): void {
    const lines = [
      'EMI ID,Amount,Mode,Reference,Status,Penalty,Days Past Due,Payment Date,Customer',
      ...this.rows().map((row) =>
        [
          row.emiId,
          row.amountPaid,
          row.paymentMode,
          row.referenceNumber,
          row.status,
          row.penalty,
          row.daysPastDue,
          row.paymentDate,
          this.resolveCustomer(row)
        ].join(',')
      )
    ];

    const blob = new Blob([lines.join('\n')], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `payment-history-${new Date().toISOString().slice(0, 10)}.csv`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  exportPdf(): void {
    const header = `PAYMENT HISTORY SNAPSHOT\nGenerated At: ${new Date().toLocaleString()}\n`;
    const body = this.rows()
      .map((row) =>
        `EMI #${row.emiId} | ${row.paymentMode} | ${row.status} | ${row.paymentDate} | Ref: ${row.referenceNumber}`
      )
      .join('\n');
    const blob = new Blob([`${header}\n${body}`], { type: 'text/plain;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = `payment-history-${new Date().toISOString().slice(0, 10)}.pdf.txt`;
    link.click();
    window.URL.revokeObjectURL(url);
  }

  private generateReceiptDocument(receipt: EmiPaymentReceiptDTO): string {
    return `
PAYMENT RECEIPT
===============================================
Receipt Number: ${receipt.receiptNumber}
Generated: ${receipt.generatedAt}

CUSTOMER DETAILS
Customer Name: ${receipt.customerName}
Customer Email: ${receipt.customerEmail}
Customer Phone: ${receipt.customerPhone}
City: ${receipt.customerCity}

LOAN DETAILS
Loan ID: ${receipt.loanId}
Loan Type: ${receipt.loanType}
Principal Amount: ${receipt.loanPrincipal}
Interest Rate: ${receipt.loanInterestRate}%
Tenure: ${receipt.loanTenureMonths} months
Disbursement Date: ${receipt.disbursementDate}

EMI DETAILS
EMI ID: ${receipt.emiId}
Installment #: ${receipt.installmentNumber}
Due Date: ${receipt.dueDate}
Amount Due: ${receipt.amountDue}
Principal Component: ${receipt.principalComponent}
Interest Component: ${receipt.interestComponent}
Penalty Amount: ${receipt.penaltyAmount}
EMI Status: ${receipt.emiStatus}

PAYMENT DETAILS
Amount Paid: ${receipt.amountPaid}
Payment Date: ${receipt.paymentDate}
Payment Mode: ${receipt.paymentMode}
Reference Number: ${receipt.referenceNumber}

===============================================
This is an electronically generated receipt.
`;
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

  private extractItems(response: EmiPaymentHistory[] | PageResponse<EmiPaymentHistory>): EmiPaymentHistory[] {
    return Array.isArray(response) ? response : response.content;
  }

  private scopeRowsByRole(items: EmiPaymentHistory[]): EmiPaymentHistory[] {
    if (this.canViewAllPayments()) {
      return items;
    }

    return items.filter((row) => {
      const data = row as unknown as { customerId?: number; customerName?: string; customerEmail?: string };
      if (this.currentUserCustomerId !== null && typeof data.customerId === 'number') {
        return data.customerId === this.currentUserCustomerId;
      }

      const rowCustomerName = String(data.customerName || '').trim().toLowerCase();
      const rowCustomerEmail = String(data.customerEmail || '').trim().toLowerCase();

      if (rowCustomerEmail && this.currentUserEmail) {
        return rowCustomerEmail === this.currentUserEmail;
      }

      if (rowCustomerName && this.currentUserName) {
        return rowCustomerName === this.currentUserName;
      }

      return false;
    });
  }

  private applyFilters(): void {
    const term = this.searchTerm().trim().toLowerCase();
    const dateFilter = this.selectedDate();
    const modeFilter = this.selectedMode();
    const statusFilter = this.selectedStatus();
    const loanFilter = this.selectedLoanId();
    const yearFilter = this.selectedYear();
    const customerFilter = this.customerTerm().trim().toLowerCase();

    const filtered = this.allRows().filter((row) => {
      const matchesTerm =
        !term ||
        String(row.emiId).includes(term) ||
        row.status.toLowerCase().includes(term) ||
        row.referenceNumber.toLowerCase().includes(term) ||
        row.paymentMode.toLowerCase().includes(term) ||
        String(row.amountPaid).includes(term) ||
        row.paymentDate.toLowerCase().includes(term);

      const matchesDate = !dateFilter || this.normalizeDate(row.paymentDate) === dateFilter;
      const matchesMode = modeFilter === 'ALL' || row.paymentMode === modeFilter;
      const matchesStatus = statusFilter === 'ALL' || row.status === statusFilter;
      const matchesLoan = loanFilter === 'ALL' || String((row as { loanId?: number }).loanId || '') === loanFilter;
      const paymentYear = this.extractYear(row.paymentDate);
      const matchesYear = yearFilter === 'ALL' || paymentYear === yearFilter;
      const matchesCustomer = !customerFilter || this.resolveCustomer(row).toLowerCase().includes(customerFilter);

      return matchesTerm && matchesDate && matchesMode && matchesStatus && matchesLoan && matchesYear && matchesCustomer;
    });

    this.updateSummaries(filtered);

    this.totalElements.set(filtered.length);
    const totalPages = Math.max(1, Math.ceil(filtered.length / this.size()));
    this.totalPages.set(totalPages);

    const currentPage = Math.min(this.page(), totalPages - 1);
    this.page.set(currentPage);

    const start = currentPage * this.size();
    this.rows.set(filtered.slice(start, start + this.size()));
  }

  resolveCustomer(row: EmiPaymentHistory): string {
    return ((row as unknown as { customerName?: string }).customerName || 'N/A').toString();
  }

  private normalizeDate(value: string): string {
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return '';
    }
    return parsed.toISOString().slice(0, 10);
  }

  private extractLoanIds(rows: EmiPaymentHistory[]): number[] {
    const unique = new Set<number>();
    rows.forEach((row) => {
      const value = Number((row as { loanId?: number }).loanId);
      if (Number.isFinite(value) && value > 0) {
        unique.add(value);
      }
    });
    return Array.from(unique).sort((left, right) => left - right);
  }

  private extractYears(rows: EmiPaymentHistory[]): string[] {
    const unique = new Set<string>();
    rows.forEach((row) => {
      const year = this.extractYear(row.paymentDate);
      if (year) {
        unique.add(year);
      }
    });
    return Array.from(unique).sort((left, right) => Number(right) - Number(left));
  }

  private extractYear(value: string): string {
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return '';
    }
    return String(parsed.getFullYear());
  }

  private updateSummaries(rows: EmiPaymentHistory[]): void {
    const totalPaid = rows.reduce((sum, row) => sum + Math.max(0, Number(row.amountPaid) || 0), 0);
    const totalPenalty = rows.reduce((sum, row) => sum + Math.max(0, Number(row.penalty) || 0), 0);
    const estimatedPrincipal = totalPaid * 0.62;
    const estimatedInterest = Math.max(0, totalPaid - estimatedPrincipal - totalPenalty);
    this.totalPaidAmount.set(totalPaid);
    this.totalPrincipalPaid.set(estimatedPrincipal);
    this.totalInterestPaid.set(estimatedInterest);
  }
}
