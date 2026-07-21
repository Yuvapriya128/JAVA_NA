import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { EmiCalculationResponse, LoanApplicationDTO, LoanProduct, LoanService } from '../../../services/loan/loan.service';
import { AuthService } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { ToastNotificationService } from '../../../services/shared/toast-notification.service';
import { StatusBadgeComponent } from '../../shared/badges/status-badge.component';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';
import { PaginationComponent } from '../../shared/pagination/pagination.component';
import { LOAN_TYPE_OPTIONS, normalizeLoanType } from '../../../constants/loan-types';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

interface ProductProfile {
  eligibility: string;
  tenure: string;
  maxAmount: string;
}

interface ProductCalculatorState {
  expanded: boolean;
  principalAmount: number;
  tenureMonths: number;
  loading: boolean;
  error: string;
  result: EmiCalculationResponse | null;
}

type ApplicationStatusFilter = 'ALL' | 'PENDING' | 'UNDER_REVIEW' | 'APPROVED' | 'REJECTED';
type ApplicationSort = 'LATEST' | 'OLDEST';

@Component({
  selector: 'app-loan-products',
  standalone: true,
  imports: [CommonModule, FormsModule, PageHeaderComponent, StatusBadgeComponent, PaginationComponent],
  templateUrl: './loan-products.component.html',
  styleUrls: ['./loan-products.component.css']})
export class LoanProductsComponent implements OnInit {
  private readonly loanService = inject(LoanService);
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);
  private readonly activatedRoute = inject(ActivatedRoute);
  private readonly toastService = inject(ToastNotificationService);

  readonly products = signal<LoanProduct[]>([]);
  readonly selectedProduct = signal<LoanProduct | null>(null);
  readonly requestedProductCodes = signal<string[]>([]);
  readonly applyingProductCode = signal<string>('');
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');
  readonly calculators = signal<Record<string, ProductCalculatorState>>({});
  readonly myApplications = signal<LoanApplicationDTO[]>([]);
  readonly myVisibleApplications = signal<LoanApplicationDTO[]>([]);
  readonly myApplicationsState = signal({ loading: false, error: '' });
  readonly myApplicationFilter = signal<ApplicationStatusFilter>('ALL');
  readonly myApplicationSort = signal<ApplicationSort>('LATEST');
  readonly myFromDate = signal('');
  readonly myToDate = signal('');
  readonly myPage = signal(0);
  readonly myPageSize = signal(5);
  readonly myPageSizeOptions = [5, 10, 20];
  readonly myTotalPages = signal(1);
  readonly myTotalRecords = signal(0);
  readonly reviewApplications = signal<LoanApplicationDTO[]>([]);
  readonly reviewQueueState = signal({ loading: false, error: '', success: '', processingId: 0 });
  readonly reviewFilter = signal<ApplicationStatusFilter>('PENDING');
  readonly approvalDrawerOpen = signal(false);
  readonly selectedReviewApplication = signal<LoanApplicationDTO | null>(null);
  readonly approvalAction = signal<'APPROVED' | 'REJECTED' | 'UNDER_REVIEW'>('APPROVED');
  readonly approvalRemark = signal('');
  readonly adminCatalogExpanded = signal(false);
  readonly requestsOnlyMode = signal(false);
  readonly latestSubmittedApplicationId = signal<number | null>(null);
  readonly productProfiles = signal<Record<string, ProductProfile>>({
    HOME: {
      eligibility: 'Salaried/self-employed with credit score 700+',
      tenure: 'Up to 30 years',
      maxAmount: 'INR 2.5 Cr'
    },
    PERSONAL: {
      eligibility: 'Stable income and clean credit profile',
      tenure: '12 to 72 months',
      maxAmount: 'INR 35 L'
    },
    VEHICLE: {
      eligibility: 'Income verification and KYC compliant',
      tenure: '12 to 84 months',
      maxAmount: 'INR 60 L'
    },
    AUTO: {
      eligibility: 'Salaried or business income with CIBIL check',
      tenure: '12 to 84 months',
      maxAmount: 'INR 45 L'
    },
    EDUCATION: {
      eligibility: 'Confirmed admission with co-applicant',
      tenure: 'Up to 10 years',
      maxAmount: 'INR 75 L'
    },
    BUSINESS: {
      eligibility: 'Business vintage 2+ years with financial statements',
      tenure: '12 to 96 months',
      maxAmount: 'INR 1.2 Cr'
    },
    GOLD: {
      eligibility: 'Approved gold collateral and KYC validation',
      tenure: '3 to 36 months',
      maxAmount: 'INR 50 L'
    },
    SECURED: {
      eligibility: 'Collateral-backed with valuation approval',
      tenure: '12 to 120 months',
      maxAmount: 'INR 3 Cr'
    },
    UNSECURED: {
      eligibility: 'Strong credit profile and stable repayments',
      tenure: '6 to 60 months',
      maxAmount: 'INR 25 L'
    }
  });

  ngOnInit(): void {
    this.requestsOnlyMode.set(this.activatedRoute.snapshot.routeConfig?.path === 'my-requests');
    this.hydrateMyRequestQueryState();

    if (this.requestsOnlyMode() && this.isEndUserFlow()) {
      if (this.myApplicationFilter() === 'ALL') {
        this.myApplicationFilter.set('PENDING');
      }
      this.syncMyRequestQueryState();
    }

    this.loadData();

    if (this.canDirectCreateLoan()) {
      this.loadReviewQueue();
    } else {
      this.primeCurrentUser();
      this.loadMyApplications();
    }
  }

  loadData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.getLoanProducts().subscribe({
      next: (response) => {
        const mergedProducts = this.mergeWithDefaultProducts(response);
        this.products.set(mergedProducts);
        this.initializeCalculators(mergedProducts);
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

  applyProduct(product: LoanProduct): void {
    this.selectedProduct.set(product);
    const normalizedLoanType = normalizeLoanType(product.code);

    if (this.tokenStorage.hasRole(['MANAGER', 'ADMIN'])) {
      this.router.navigate(['/create-loan'], { queryParams: { loanType: normalizedLoanType } });
      return;
    }

    if (this.isAlreadyRequested(normalizedLoanType)) {
      this.status.set({
        loading: false,
        success: false,
        error: `You have already requested ${product.displayName}. Track it from My Requests.`
      });
      this.successMessage.set('');
      return;
    }

    // For USER role: apply for loan
    const request = {
      loanType: normalizedLoanType,
      customerId: this.authService.getCurrentUserSync()?.customerId,
      annualInterestRate: product.defaultRate,
      principalAmount: 100000,
      tenureMonths: 12
    };

    this.applyingProductCode.set(normalizedLoanType);
    this.status.set({ loading: false, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.applyForLoan(request).subscribe({
      next: (response) => {
        this.requestedProductCodes.set([...this.requestedProductCodes(), normalizedLoanType]);
        this.myApplications.set([response, ...this.myApplications()]);
        this.latestSubmittedApplicationId.set(response.applicationId);
        this.applyingProductCode.set('');
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set(`Application submitted for ${product.displayName}. Application ID: ${response.applicationId}. Track status in My Requests.`);
        this.scrollToApplicationRow(response.applicationId);
        this.toastService.show(`Loan application submitted successfully!`, 'success');
      },
      error: (error) => {
        const message = String(error?.error?.message || error?.message || 'Failed to submit application');
        const duplicateError = error?.status === 409 || message.toLowerCase().includes('duplicate');

        if (duplicateError) {
          this.loadMyApplications();
        }

        this.applyingProductCode.set('');
        this.status.set({
          loading: false,
          success: false,
          error: duplicateError
            ? `You already requested ${product.displayName}. Duplicate request is blocked by backend.`
            : message
        });
        this.successMessage.set('');
        this.toastService.show(duplicateError ? 'Duplicate request blocked' : 'Failed to submit loan application', 'danger');
      }
    });
  }

  canDirectCreateLoan(): boolean {
    return this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }

  shouldShowPrimaryError(): boolean {
    if (!this.status().error) {
      return false;
    }
    return !(this.canDirectCreateLoan() && !!this.reviewQueueState().error);
  }

  pageTitle(): string {
    if (this.requestsOnlyMode() && this.isEndUserFlow()) {
      return 'My Requests';
    }
    return 'Loan Products';
  }

  pageSubtitle(): string {
    if (this.requestsOnlyMode() && this.isEndUserFlow()) {
      return 'Track Product Request Status';
    }
    return 'Product Catalog';
  }

  showProductCatalog(): boolean {
    if (this.canDirectCreateLoan()) {
      return this.adminCatalogExpanded();
    }
    return !this.requestsOnlyMode();
  }

  toggleAdminCatalog(): void {
    this.adminCatalogExpanded.set(!this.adminCatalogExpanded());
  }

  isEndUserFlow(): boolean {
    return !this.canDirectCreateLoan();
  }

  showMyRequestsSection(): boolean {
    return this.isEndUserFlow() && this.requestsOnlyMode();
  }

  isProductApplying(product: LoanProduct): boolean {
    return this.applyingProductCode() === normalizeLoanType(product.code);
  }

  isProductRequested(product: LoanProduct): boolean {
    return this.isAlreadyRequested(normalizeLoanType(product.code));
  }

  isAlreadyRequested(loanType: string): boolean {
    return this.requestedProductCodes().includes(loanType);
  }

  filteredMyApplications(): LoanApplicationDTO[] {
    return this.myVisibleApplications();
  }

  private applyMyRequestFilters(): void {
    const filter = this.myApplicationFilter();
    const from = this.myFromDate();
    const to = this.myToDate();
    const sorted = [...this.myApplications()].filter((row) => {
      if (filter !== 'ALL' && this.normalizeApplicationStatus(row.applicationStatus) !== filter) {
        return false;
      }

      const rowDate = this.normalizeDateOnly(row.applicationDate);
      if (from && rowDate < from) {
        return false;
      }
      if (to && rowDate > to) {
        return false;
      }

      return true;
    });

    sorted.sort((left, right) => {
      const leftTime = new Date(left.applicationDate).getTime();
      const rightTime = new Date(right.applicationDate).getTime();
      const delta = (Number.isFinite(leftTime) ? leftTime : 0) - (Number.isFinite(rightTime) ? rightTime : 0);
      return this.myApplicationSort() === 'LATEST' ? -delta : delta;
    });

    this.myTotalRecords.set(sorted.length);
    const pages = Math.max(1, Math.ceil(sorted.length / this.myPageSize()));
    this.myTotalPages.set(pages);

    const page = Math.min(this.myPage(), pages - 1);
    this.myPage.set(page);
    const start = page * this.myPageSize();
    this.myVisibleApplications.set(sorted.slice(start, start + this.myPageSize()));
  }

  setMyApplicationFilter(filter: ApplicationStatusFilter): void {
    this.myApplicationFilter.set(filter);
    this.myPage.set(0);
    this.applyMyRequestFilters();
    this.syncMyRequestQueryState();
  }

  setMyApplicationSort(sort: ApplicationSort): void {
    this.myApplicationSort.set(sort);
    this.myPage.set(0);
    this.applyMyRequestFilters();
    this.syncMyRequestQueryState();
  }

  setMyFromDate(value: string): void {
    this.myFromDate.set(value);
    this.myPage.set(0);
    this.applyMyRequestFilters();
    this.syncMyRequestQueryState();
  }

  setMyToDate(value: string): void {
    this.myToDate.set(value);
    this.myPage.set(0);
    this.applyMyRequestFilters();
    this.syncMyRequestQueryState();
  }

  onMyPageChange(page: number): void {
    this.myPage.set(page);
    this.applyMyRequestFilters();
    this.syncMyRequestQueryState();
  }

  onMyPageSizeChange(size: number): void {
    this.myPageSize.set(size);
    this.myPage.set(0);
    this.applyMyRequestFilters();
    this.syncMyRequestQueryState();
  }

  requestCountByStatus(filter: ApplicationStatusFilter): number {
    if (filter === 'ALL') {
      return this.myApplications().length;
    }

    return this.myApplications().filter((row) => this.normalizeApplicationStatus(row.applicationStatus) === filter).length;
  }

  refreshMyApplications(): void {
    this.loadMyApplications();
  }

  openLoanProductsCatalog(): void {
    this.router.navigate(['/loan-products']);
  }

  reapplyForRejected(application: LoanApplicationDTO): void {
    if (this.normalizeApplicationStatus(application.applicationStatus) !== 'REJECTED') {
      return;
    }

    const normalizedLoanType = normalizeLoanType(application.loanType);
    if (this.isAlreadyRequested(normalizedLoanType)) {
      this.status.set({
        loading: false,
        success: false,
        error: `You already have an open request for ${normalizedLoanType}.`
      });
      this.successMessage.set('');
      return;
    }

    this.applyingProductCode.set(normalizedLoanType);
    this.status.set({ loading: false, success: false, error: '' });
    this.successMessage.set('');
    this.loanService.applyForLoan({
      loanType: normalizedLoanType,
      customerId: application.customerId,
      annualInterestRate: application.annualInterestRate,
      principalAmount: application.principalAmount,
      tenureMonths: application.tenureMonths
    }).subscribe({
      next: (response) => {
        this.requestedProductCodes.set([...this.requestedProductCodes(), normalizedLoanType]);
        this.myApplications.set([response, ...this.myApplications()]);
        this.latestSubmittedApplicationId.set(response.applicationId);
        this.applyingProductCode.set('');
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set(`Re-application submitted successfully. New Application ID: ${response.applicationId}.`);
        this.toastService.show('Re-application submitted successfully', 'success');
        this.scrollToApplicationRow(response.applicationId);
      },
      error: (error) => {
        this.applyingProductCode.set('');
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Failed to submit re-application.'
        });
        this.successMessage.set('');
        this.toastService.show('Failed to submit re-application', 'danger');
      }
    });
  }

  canReapplyForRejected(application: LoanApplicationDTO): boolean {
    return this.normalizeApplicationStatus(application.applicationStatus) === 'REJECTED';
  }

  filteredReviewApplications(): LoanApplicationDTO[] {
    const filter = this.reviewFilter();
    const rows = this.reviewApplications();
    if (filter === 'ALL') {
      return rows;
    }
    return rows.filter((row) => this.normalizeApplicationStatus(row.applicationStatus) === filter);
  }

  isReviewProcessing(applicationId: number): boolean {
    return this.reviewQueueState().processingId === applicationId;
  }

  canReview(application: LoanApplicationDTO): boolean {
    const status = this.normalizeApplicationStatus(application.applicationStatus);
    return status === 'PENDING' || status === 'UNDER_REVIEW';
  }

  approveApplication(application: LoanApplicationDTO): void {
    this.openApprovalDrawer(application, 'APPROVED');
  }

  createLoanFromApplication(application: LoanApplicationDTO): void {
    this.router.navigate(['/create-loan'], {
      queryParams: {
        loanType: normalizeLoanType(application.loanType),
        customerId: application.customerId,
        principalAmount: application.principalAmount,
        tenureMonths: application.tenureMonths,
        annualInterestRate: application.annualInterestRate,
        applicationId: application.applicationId
      }
    });
  }

  rejectApplication(application: LoanApplicationDTO): void {
    this.openApprovalDrawer(application, 'REJECTED');
  }

  openApprovalDrawer(application: LoanApplicationDTO, action: 'APPROVED' | 'REJECTED' | 'UNDER_REVIEW' = 'APPROVED'): void {
    this.selectedReviewApplication.set(application);
    this.approvalAction.set(action);
    this.approvalRemark.set('');
    this.approvalDrawerOpen.set(true);
  }

  closeApprovalDrawer(): void {
    this.approvalDrawerOpen.set(false);
    this.selectedReviewApplication.set(null);
    this.approvalRemark.set('');
    this.approvalAction.set('APPROVED');
  }

  submitApprovalDecision(): void {
    const selected = this.selectedReviewApplication();
    if (!selected) {
      return;
    }
    const action = this.approvalAction();
    const remark = this.approvalRemark().trim();
    if ((action === 'REJECTED' || action === 'UNDER_REVIEW') && !remark) {
      this.toastService.show('Remarks are required for this action', 'warning');
      return;
    }
    const remarkValue = remark || undefined;
    this.processApplicationDecision(selected, action, remarkValue);
    this.closeApprovalDrawer();
  }

  formatDate(value: string): string {
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }
    return parsed.toLocaleString('en-IN', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  pendingReviewCount(): number {
    return this.reviewApplications().filter((row) => this.normalizeApplicationStatus(row.applicationStatus) === 'PENDING').length;
  }

  applicationNextStep(application: LoanApplicationDTO): string {
    const status = this.normalizeApplicationStatus(application.applicationStatus);
    if (status === 'APPROVED') {
      return 'Approved. Manager/Admin can proceed to create the loan.';
    }
    if (status === 'REJECTED') {
      return application.rejectionReason ? `Rejected: ${application.rejectionReason}` : 'Rejected by manager/admin.';
    }
    return 'Waiting for manager/admin review.';
  }

  rejectedImprovementTips(application: LoanApplicationDTO): string[] {
    if (this.normalizeApplicationStatus(application.applicationStatus) !== 'REJECTED') {
      return [];
    }

    const reason = String(application.rejectionReason || '').toLowerCase();
    if (reason.includes('credit')) {
      return ['Improve credit score and reduce current utilization.', 'Attach latest income proof and bank statements.'];
    }
    if (reason.includes('document') || reason.includes('kyc')) {
      return ['Re-upload complete KYC and address proof documents.', 'Ensure submitted documents are valid and readable.'];
    }
    if (reason.includes('income')) {
      return ['Provide updated salary slips or GST returns.', 'Choose a lower principal or longer tenure and re-apply.'];
    }

    return ['Review the rejection reason carefully before re-applying.', 'Consider reducing requested amount or increasing tenure.'];
  }

  timelineStepClass(application: LoanApplicationDTO, step: 'submitted' | 'review' | 'decision'): string {
    const status = this.normalizeApplicationStatus(application.applicationStatus);
    if (step === 'submitted') {
      return 'done';
    }
    if (step === 'review') {
      if (status === 'UNDER_REVIEW' || status === 'APPROVED' || status === 'REJECTED') {
        return 'done';
      }
      return 'current';
    }
    if (status === 'APPROVED' || status === 'REJECTED') {
      return 'current';
    }
    return 'upcoming';
  }

  timelineDecisionLabel(application: LoanApplicationDTO): string {
    const status = this.normalizeApplicationStatus(application.applicationStatus);
    if (status === 'APPROVED') {
      return 'Approved';
    }
    if (status === 'REJECTED') {
      return 'Rejected';
    }
    return 'Decision';
  }

  isLatestSubmitted(applicationId: number): boolean {
    return this.latestSubmittedApplicationId() === applicationId;
  }

  onReviewFilterChange(filter: ApplicationStatusFilter): void {
    this.reviewFilter.set(filter);
    this.loadReviewQueue();
  }

  profileFor(product: LoanProduct): ProductProfile {
    return this.productProfiles()[normalizeLoanType(product.code)] || {
      eligibility: 'Standard underwriting policy applies',
      tenure: 'As per product policy',
      maxAmount: 'As per eligibility'
    };
  }

  toggleCalculator(product: LoanProduct): void {
    const code = normalizeLoanType(product.code);
    const current = this.calculators()[code] || this.defaultCalculatorState();
    this.calculators.set({
      ...this.calculators(),
      [code]: {
        ...current,
        expanded: !current.expanded
      }
    });
  }

  calculatorFor(product: LoanProduct): ProductCalculatorState {
    const code = normalizeLoanType(product.code);
    return this.calculators()[code] || this.defaultCalculatorState();
  }

  onCalculatorPrincipalChange(product: LoanProduct, value: number): void {
    this.patchCalculator(product, {
      principalAmount: value,
      error: '',
      result: null
    });
  }

  onCalculatorTenureChange(product: LoanProduct, value: number): void {
    this.patchCalculator(product, {
      tenureMonths: value,
      error: '',
      result: null
    });
  }

  calculateProductEmi(product: LoanProduct): void {
    const code = normalizeLoanType(product.code);
    const current = this.calculatorFor(product);

    if (!current.principalAmount || current.principalAmount <= 0 || !current.tenureMonths || current.tenureMonths <= 0) {
      this.patchCalculator(product, {
        loading: false,
        error: 'Enter valid principal amount and tenure for EMI estimation.',
        result: null
      });
      return;
    }

    this.patchCalculator(product, { loading: true, error: '', result: null });
    this.loanService.calculateEmi({
      principalAmount: current.principalAmount,
      annualInterestRate: product.defaultRate,
      tenureMonths: current.tenureMonths
    }).subscribe({
      next: (response) => {
        this.patchCalculatorByCode(code, {
          loading: false,
          error: '',
          result: response
        });
      },
      error: (error) => {
        this.patchCalculatorByCode(code, {
          loading: false,
          error: error?.error?.message || error?.message || 'Failed to calculate EMI estimate.',
          result: null
        });
      }
    });
  }

  formatCurrency(value: number): string {
    return `INR ${value.toLocaleString('en-IN', { maximumFractionDigits: 2 })}`;
  }

  private loadMyApplications(): void {
    this.myApplicationsState.set({ loading: true, error: '' });
    this.loanService.getMyLoanApplications(0, 50).subscribe({
      next: (response) => {
        const rows = [...response.content].sort((a, b) => this.sortByDateDesc(a.applicationDate, b.applicationDate));
        this.myApplications.set(rows);
        this.rebuildRequestedCodes(rows);
        this.applyMyRequestFilters();
        this.myApplicationsState.set({ loading: false, error: '' });
      },
      error: (error) => {
        this.myApplicationsState.set({
          loading: false,
          error: error?.error?.message || error?.message || 'Unable to load your product requests right now.'
        });
      }
    });
  }

  loadReviewQueue(): void {
    this.reviewQueueState.set({ loading: true, error: '', success: '', processingId: 0 });
    this.loanService.getLoanApplications(0, 100, this.reviewFilter()).subscribe({
      next: (response) => {
        const rows = [...response.content].sort((a, b) => this.sortByDateDesc(a.applicationDate, b.applicationDate));
        this.reviewApplications.set(rows);
        this.reviewQueueState.set({ loading: false, error: '', success: '', processingId: 0 });
      },
      error: (error) => {
        this.reviewQueueState.set({
          loading: false,
          error: error?.error?.message || error?.message ||
            'Review queue endpoint is not available yet. Ask backend to expose loan product application queue APIs.',
          success: '',
          processingId: 0
        });
      }
    });
  }

  private primeCurrentUser(): void {
    if (this.authService.getCurrentUserSync()) {
      return;
    }

    this.authService.getCurrentUser().subscribe({
      next: () => {
        // cache warmed in auth service
      },
      error: () => {
        // Keep request flow non-blocking if current user endpoint is unavailable.
      }
    });
  }

  private processApplicationDecision(application: LoanApplicationDTO, status: 'APPROVED' | 'REJECTED' | 'UNDER_REVIEW', rejectionReason?: string): void {
    this.reviewQueueState.set({
      ...this.reviewQueueState(),
      processingId: application.applicationId,
      success: '',
      error: ''
    });

    this.loanService.updateLoanApplicationStatus(application.applicationId, { status, rejectionReason }).subscribe({
      next: () => {
        const approvedNow = status === 'APPROVED';

        this.reviewQueueState.set({
          loading: false,
          error: '',
          success: `Application #${application.applicationId} ${status === 'UNDER_REVIEW' ? 'sent for additional documents' : status.toLowerCase()}.`,
          processingId: 0
        });

        if (approvedNow) {
          this.reviewFilter.set('APPROVED');
        }
        this.loadReviewQueue();

        if (approvedNow) {
          this.createLoanFromApplication({
            ...application,
            applicationStatus: 'APPROVED'
          });
        }
      },
      error: (error) => {
        this.reviewQueueState.set({
          ...this.reviewQueueState(),
          processingId: 0,
          error: error?.error?.message || error?.message || `Failed to ${status.toLowerCase()} application.`,
          success: ''
        });
      }
    });
  }

  private rebuildRequestedCodes(applications: LoanApplicationDTO[]): void {
    const openStatuses = new Set(['PENDING', 'UNDER_REVIEW', 'APPROVED']);
    const requestedCodes = applications
      .filter((item) => openStatuses.has(this.normalizeApplicationStatus(item.applicationStatus)))
      .map((item) => normalizeLoanType(item.loanType));
    this.requestedProductCodes.set(Array.from(new Set(requestedCodes)));
  }

  private scrollToApplicationRow(applicationId: number): void {
    setTimeout(() => {
      const element = document.getElementById(`request-row-${applicationId}`);
      if (!element) {
        return;
      }
      element.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }, 120);
  }

  private normalizeApplicationStatus(status: string): ApplicationStatusFilter {
    const normalized = String(status || '').trim().toUpperCase();
    if (normalized === 'APPROVED') {
      return 'APPROVED';
    }
    if (normalized === 'REJECTED') {
      return 'REJECTED';
    }
    if (normalized === 'UNDER_REVIEW' || normalized === 'IN_REVIEW') {
      return 'UNDER_REVIEW';
    }
    return 'PENDING';
  }

  private hydrateMyRequestQueryState(): void {
    const params = this.activatedRoute.snapshot.queryParamMap;
    const status = String(params.get('status') || '').toUpperCase() as ApplicationStatusFilter;
    const sort = String(params.get('sort') || '').toUpperCase() as ApplicationSort;
    const fromDate = String(params.get('from') || '');
    const toDate = String(params.get('to') || '');
    const page = Number(params.get('page'));
    const size = Number(params.get('size'));

    if (['ALL', 'PENDING', 'UNDER_REVIEW', 'APPROVED', 'REJECTED'].includes(status)) {
      this.myApplicationFilter.set(status);
    }
    if (sort === 'LATEST' || sort === 'OLDEST') {
      this.myApplicationSort.set(sort);
    }
    if (fromDate) {
      this.myFromDate.set(fromDate);
    }
    if (toDate) {
      this.myToDate.set(toDate);
    }
    if (Number.isFinite(page) && page >= 0) {
      this.myPage.set(page);
    }
    if (Number.isFinite(size) && this.myPageSizeOptions.includes(size)) {
      this.myPageSize.set(size);
    }
  }

  private syncMyRequestQueryState(): void {
    if (!this.requestsOnlyMode()) {
      return;
    }

    this.router.navigate([], {
      relativeTo: this.activatedRoute,
      queryParams: {
        status: this.myApplicationFilter(),
        sort: this.myApplicationSort(),
        from: this.myFromDate() || null,
        to: this.myToDate() || null,
        page: this.myPage() || null,
        size: this.myPageSize() === 5 ? null : this.myPageSize()
      },
      replaceUrl: true
    });
  }

  private normalizeDateOnly(value: string): string {
    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return '';
    }
    return parsed.toISOString().slice(0, 10);
  }

  private sortByDateDesc(a: string, b: string): number {
    const left = new Date(a).getTime();
    const right = new Date(b).getTime();
    if (Number.isNaN(left) || Number.isNaN(right)) {
      return 0;
    }
    return right - left;
  }

  private mergeWithDefaultProducts(response: LoanProduct[]): LoanProduct[] {
    const responseByCode = new Map<string, LoanProduct>();

    response.forEach((product) => {
      responseByCode.set(normalizeLoanType(product.code), {
        ...product,
        code: normalizeLoanType(product.code)
      });
    });

    const mergedDefaults = LOAN_TYPE_OPTIONS.map((item) => {
      const existing = responseByCode.get(item.code);
      if (existing) {
        return existing;
      }

      return {
        code: item.code,
        displayName: item.label,
        defaultRate: item.defaultRate || 10
      } as LoanProduct;
    });

    return mergedDefaults;
  }

  private initializeCalculators(products: LoanProduct[]): void {
    const existing = this.calculators();
    const nextState: Record<string, ProductCalculatorState> = {};

    products.forEach((product) => {
      const code = normalizeLoanType(product.code);
      nextState[code] = existing[code] || this.defaultCalculatorState();
    });

    this.calculators.set(nextState);
  }

  private defaultCalculatorState(): ProductCalculatorState {
    return {
      expanded: false,
      principalAmount: 0,
      tenureMonths: 0,
      loading: false,
      error: '',
      result: null
    };
  }

  private patchCalculator(product: LoanProduct, patch: Partial<ProductCalculatorState>): void {
    this.patchCalculatorByCode(normalizeLoanType(product.code), patch);
  }

  private patchCalculatorByCode(code: string, patch: Partial<ProductCalculatorState>): void {
    const current = this.calculators()[code] || this.defaultCalculatorState();
    this.calculators.set({
      ...this.calculators(),
      [code]: {
        ...current,
        ...patch
      }
    });
  }
}
