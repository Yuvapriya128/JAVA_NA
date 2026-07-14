import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { CustomerService } from '../../../../core/services/customer.service';
import { finalize, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { PERMISSIONS } from '../../../../core/auth/constants/permissions.constants';
import { ROLES } from '../../../../core/auth/constants/roles.constants';

@Component({
  selector: 'app-customer-view',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './customer-view.component.html',
  styleUrl: './customer-view.component.css'})
export class CustomerViewComponent implements OnInit, OnDestroy {
  customer: any;
  isLoading = true;
  customerId: number = 0;
  errorMessage = '';
  private destroy$ = new Subject<void>();
  private readonly authState = inject(AuthStateService);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    console.log('[CustomerViewComponent] View Component Loaded');

    // Subscribe to route param changes (not just snapshot)
    // This ensures loadCustomer is called when navigating between customer views
    this.route.paramMap
      .pipe(
        distinctUntilChanged((prev, curr) => prev.get('id') === curr.get('id')),
        takeUntil(this.destroy$)
      )
      .subscribe(params => {
        const id = params.get('id');
        console.log('[CustomerViewComponent] Route param changed - ID:', id);

        if (!id || isNaN(+id) || +id <= 0) {
          console.warn('[CustomerViewComponent] Invalid ID:', id);
          this.isLoading = false;
          this.errorMessage = 'Invalid customer ID';
          this.cdr.detectChanges();
          return;
        }

        this.customerId = +id;
        this.loadCustomer();
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCustomer(): void {
    // Validate route parameter
    if (!this.customerId || this.customerId <= 0 || isNaN(this.customerId)) {
      console.warn('[CustomerViewComponent] Invalid ID:', this.customerId);
      this.isLoading = false;
      this.errorMessage = 'Invalid customer ID';
      this.cdr.detectChanges();
      return;
    }

    console.log('[CustomerViewComponent] Calling getById:', this.customerId);
    this.customerService.getCustomerById(this.customerId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          console.log('[CustomerViewComponent] API Response:', data);
          this.customer = data;
          this.errorMessage = '';
          console.log('[CustomerViewComponent] Assigned Object:', this.customer);
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[CustomerViewComponent] API Error:', error);
          this.customer = null;

          if (error.status === 404) {
            this.errorMessage = 'Customer not found';
          } else if (error.status === 403) {
            this.errorMessage = 'Access denied';
          } else {
            this.errorMessage = 'Error loading customer: ' + (error?.error?.message || error?.message || 'Unknown error');
          }
          this.cdr.detectChanges();
        }
      });
  }

  onEdit(): void {
    if (!this.canEditCustomer()) {
      return;
    }
    this.router.navigate(['/customers/edit', this.customerId]);
  }

  onDelete(): void {
    if (!this.canDeleteCustomer()) {
      return;
    }
    if (confirm('Are you sure?')) {
      this.customerService.deleteCustomer(this.customerId).pipe(
        takeUntil(this.destroy$)
      ).subscribe({
        next: () => {
          this.router.navigate(['/customers']);
        },
        error: (error) => alert('Delete failed: ' + error?.error?.message)
      });
    }
  }

  onChangeRole(): void {
    if (!this.canManageRoles() || !this.customer) {
      return;
    }

    const selectedRole = prompt(`Change role to (${ROLES.ADMIN}, ${ROLES.MANAGER}, ${ROLES.USER})`, this.customer.role || ROLES.USER);
    if (!selectedRole) {
      return;
    }

    const normalized = selectedRole.trim().toUpperCase();
    if (![ROLES.ADMIN, ROLES.MANAGER, ROLES.USER].includes(normalized as any)) {
      alert('Invalid role selected.');
      return;
    }

    this.customerService.changeRole(this.customerId, { role: normalized })
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.loadCustomer();
        },
        error: (error) => {
          alert('Role update failed: ' + (error?.error?.message || 'Unknown error'));
        }
      });
  }

  onBack(): void {
    this.router.navigate(['/customers']);
  }

  canEditCustomer(): boolean {
    return this.authState.hasPermission(PERMISSIONS.CUSTOMER_UPDATE);
  }

  canDeleteCustomer(): boolean {
    return this.authState.hasPermission(PERMISSIONS.CUSTOMER_DELETE);
  }

  canManageRoles(): boolean {
    return this.authState.hasPermission(PERMISSIONS.CUSTOMER_ROLE_UPDATE);
  }
}
