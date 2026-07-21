import { CommonModule } from '@angular/common';
import { Component, Input, OnInit, inject, signal } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { DashboardService } from '../../../services/dashboard/dashboard.service';
import { LoanApplicationDTO, LoanService } from '../../../services/loan/loan.service';

type AppRole = 'USER' | 'MANAGER' | 'ADMIN';
type SidebarBadgeKey = 'notifications' | 'pendingApplications';

interface SidebarMenuItem {
  label: string;
  icon: string;
  route: string;
  queryParams?: Record<string, string>;
  exact?: boolean;
  badgeKey?: SidebarBadgeKey;
}

interface SidebarSection {
  title: string;
  items: SidebarMenuItem[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']})
export class SidebarComponent implements OnInit {
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly loanService = inject(LoanService);
  private readonly dashboardService = inject(DashboardService);

  @Input() collapsed = false;
  readonly pendingApplicationsCount = signal(0);
  readonly notificationCount = signal(0);

  ngOnInit(): void {
    this.loadBadgeCounts();
  }

  canAccessDashboard(): boolean {
    return this.tokenStorage.isLoggedIn();
  }

  sectionGroups(): SidebarSection[] {
    const role = this.resolveRole();
    if (role === 'ADMIN') {
      return this.adminSections();
    }
    if (role === 'MANAGER') {
      return this.managerSections();
    }
    return this.userSections();
  }

  getBadgeValue(key?: SidebarBadgeKey): number {
    if (key === 'pendingApplications') {
      return this.pendingApplicationsCount();
    }
    if (key === 'notifications') {
      return this.notificationCount();
    }
    return 0;
  }

  displayBadge(value: number): string {
    if (value > 99) {
      return '99+';
    }
    return String(value);
  }

  private userSections(): SidebarSection[] {
    return [
      {
        title: 'HOME',
        items: [
          { label: 'Dashboard', icon: 'bi bi-grid-1x2', route: '/dashboard', exact: true }
        ]
      },
      {
        title: 'MY LOANS',
        items: [
          { label: 'My Loans', icon: 'bi bi-cash-coin', route: '/loans' },
          { label: 'Loan Applications', icon: 'bi bi-list-check', route: '/my-requests', badgeKey: 'pendingApplications' },
          { label: 'EMI & Payments', icon: 'bi bi-receipt-cutoff', route: '/payments' }
        ]
      },
      {
        title: 'QUICK ACTIONS',
        items: [
          { label: 'Apply for Loan', icon: 'bi bi-diagram-3', route: '/applications' },
          { label: 'Pay EMI', icon: 'bi bi-wallet2', route: '/emi/pay' },
          { label: 'Foreclosure / Loan Closure', icon: 'bi bi-check2-square', route: '/loans/foreclosure' }
        ]
      },
      {
        title: 'ACCOUNT',
        items: [
          { label: 'Notifications', icon: 'bi bi-bell', route: '/notifications', badgeKey: 'notifications' },
          { label: 'Help & Support', icon: 'bi bi-life-preserver', route: '/support' },
          { label: 'Profile', icon: 'bi bi-person-badge', route: '/profile' },
          { label: 'Settings', icon: 'bi bi-gear', route: '/settings' }
        ]
      }
    ];
  }

  private managerSections(): SidebarSection[] {
    return [
      {
        title: 'OVERVIEW',
        items: [
          { label: 'Dashboard', icon: 'bi bi-grid-1x2', route: '/dashboard', exact: true }
        ]
      },
      {
        title: 'CUSTOMERS',
        items: [
          { label: 'Customers', icon: 'bi bi-people', route: '/customers' },
          { label: 'Customer Requests', icon: 'bi bi-person-lines-fill', route: '/applications', queryParams: { scope: 'customer-requests' }, badgeKey: 'pendingApplications' }
        ]
      },
      {
        title: 'LOANS',
        items: [
          { label: 'Loan Applications', icon: 'bi bi-diagram-3', route: '/applications', badgeKey: 'pendingApplications' },
          { label: 'Loans', icon: 'bi bi-cash-coin', route: '/loans' },
          { label: 'EMI Payments', icon: 'bi bi-receipt-cutoff', route: '/payments' }
        ]
      },
      {
        title: 'OPERATIONS',
        items: [
          { label: 'Reports', icon: 'bi bi-bar-chart-line', route: '/reports' },
          { label: 'Interest Management', icon: 'bi bi-sliders', route: '/update-interest' },
          { label: 'Collections', icon: 'bi bi-collection', route: '/payments', queryParams: { view: 'collections' } },
          { label: 'Foreclosure', icon: 'bi bi-check2-square', route: '/loans/foreclosure' }
        ]
      },
      {
        title: 'ACCOUNT',
        items: [
          { label: 'Notifications', icon: 'bi bi-bell', route: '/notifications', badgeKey: 'notifications' },
          { label: 'Help & Support', icon: 'bi bi-life-preserver', route: '/support' },
          { label: 'Profile', icon: 'bi bi-person-badge', route: '/profile' },
          { label: 'Settings', icon: 'bi bi-gear', route: '/settings' }
        ]
      }
    ];
  }

  private adminSections(): SidebarSection[] {
    return [
      {
        title: 'OVERVIEW',
        items: [
          { label: 'Dashboard', icon: 'bi bi-grid-1x2', route: '/dashboard', exact: true }
        ]
      },
      {
        title: 'LENDING',
        items: [
          { label: 'Customers', icon: 'bi bi-people', route: '/customers' },
          { label: 'Applications', icon: 'bi bi-diagram-3', route: '/applications', badgeKey: 'pendingApplications' },
          { label: 'Loans', icon: 'bi bi-cash-coin', route: '/loans' }
        ]
      },
      {
        title: 'OPERATIONS',
        items: [
          { label: 'Collections', icon: 'bi bi-collection', route: '/payments', queryParams: { view: 'collections' } },
          { label: 'Reports', icon: 'bi bi-bar-chart-line', route: '/reports' }
        ]
      },
      {
        title: 'SYSTEM',
        items: [
          { label: 'Administration', icon: 'bi bi-shield-lock', route: '/administration' }
        ]
      },
      {
        title: 'ACCOUNT',
        items: [
          { label: 'Notifications', icon: 'bi bi-bell', route: '/notifications', badgeKey: 'notifications' },
          { label: 'Profile', icon: 'bi bi-person-badge', route: '/profile' }
        ]
      }
    ];
  }

  private loadBadgeCounts(): void {
    const role = this.resolveRole();
    if (role === 'USER') {
      this.loanService.getMyLoanApplications(0, 100).subscribe({
        next: (response) => {
          const pending = response.content.filter((item) => this.isPendingStatus(item)).length;
          this.pendingApplicationsCount.set(pending);
          this.notificationCount.set(pending);
        },
        error: () => {
          this.pendingApplicationsCount.set(0);
          this.notificationCount.set(0);
        }
      });
      return;
    }

    this.loanService.getLoanApplications(0, 100, 'ALL').subscribe({
      next: (response) => {
        const pending = response.content.filter((item) => this.isPendingStatus(item)).length;
        this.pendingApplicationsCount.set(pending);
      },
      error: () => {
        this.pendingApplicationsCount.set(0);
      }
    });

    const metricsRequest = role === 'ADMIN'
      ? this.dashboardService.getAdminDashboard()
      : this.dashboardService.getDashboard();

    metricsRequest.subscribe({
      next: (response) => {
        const overdue = Math.max(0, Number(response.overdueEMIs) || 0);
        this.notificationCount.set(overdue);
      },
      error: () => {
        this.notificationCount.set(0);
      }
    });
  }

  private isPendingStatus(item: LoanApplicationDTO): boolean {
    const status = String(item.applicationStatus || '').trim().toUpperCase();
    return status === 'PENDING' || status === 'UNDER_REVIEW' || status === 'IN_REVIEW';
  }

  private resolveRole(): AppRole {
    const rawRole = String(this.tokenStorage.getPrimaryRole() || '').replace('ROLE_', '');
    if (rawRole === 'ADMIN' || rawRole === 'MANAGER') {
      return rawRole;
    }
    return 'USER';
  }
}
