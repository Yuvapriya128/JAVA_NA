import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CustomerResponse, CustomerService } from '../../../services/customer/customer.service';
import { LoanApplicationDTO, LoanService } from '../../../services/loan/loan.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

interface AdminAuditRow {
  timestamp: string;
  event: string;
  actor: string;
  status: string;
}

@Component({
  selector: 'app-admin-center',
  standalone: true,
  imports: [CommonModule, RouterLink, PageHeaderComponent],
  templateUrl: './admin-center.component.html',
  styleUrls: ['./admin-center.component.css']
})
export class AdminCenterComponent implements OnInit {
  private readonly customerService = inject(CustomerService);
  private readonly loanService = inject(LoanService);

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly customers = signal<CustomerResponse[]>([]);
  readonly auditRows = signal<AdminAuditRow[]>([]);
  readonly activeTab = signal<'users' | 'policies' | 'audit'>('users');

  ngOnInit(): void {
    this.loadAdministrationData();
  }

  setTab(tab: 'users' | 'policies' | 'audit'): void {
    this.activeTab.set(tab);
  }

  totalUsers(): number {
    return this.customers().length;
  }

  activeUsers(): number {
    return this.customers().filter((row) => !!row.active).length;
  }

  managerCount(): number {
    return this.customers().filter((row) => this.normalizeRole(row.role) === 'MANAGER').length;
  }

  adminCount(): number {
    return this.customers().filter((row) => this.normalizeRole(row.role) === 'ADMIN').length;
  }

  userCount(): number {
    return this.customers().filter((row) => this.normalizeRole(row.role) === 'USER').length;
  }

  private loadAdministrationData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.customerService.getAllCustomers(0, 5000).subscribe({
      next: (customerResponse) => {
        this.customers.set(customerResponse.content);
        this.loadAuditLogs();
      },
      error: (error: { error?: { message?: string }; message?: string }) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Unable to load user management data.'
        });
      }
    });
  }

  private loadAuditLogs(): void {
    this.loanService.getLoanApplications(0, 100, 'ALL').subscribe({
      next: (applicationsResponse) => {
        const rows = applicationsResponse.content
          .sort((left, right) => new Date(right.applicationDate).getTime() - new Date(left.applicationDate).getTime())
          .slice(0, 20)
          .map((item) => this.toAuditRow(item));
        this.auditRows.set(rows);
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: () => {
        this.auditRows.set([]);
        this.status.set({ loading: false, success: false, error: '' });
      }
    });
  }

  private toAuditRow(item: LoanApplicationDTO): AdminAuditRow {
    const status = String(item.applicationStatus || '').toUpperCase();
    const event = status === 'APPROVED'
      ? `Application #${item.applicationId} approved for ${item.customerName}`
      : status === 'REJECTED'
        ? `Application #${item.applicationId} rejected for ${item.customerName}`
        : `Application #${item.applicationId} submitted by ${item.customerName}`;
    return {
      timestamp: item.applicationDate,
      event,
      actor: `Customer #${item.customerId}`,
      status
    };
  }

  private normalizeRole(value: string): 'USER' | 'MANAGER' | 'ADMIN' {
    const normalized = String(value || '').trim().toUpperCase().replace('ROLE_', '');
    if (normalized === 'ADMIN') {
      return 'ADMIN';
    }
    if (normalized === 'MANAGER') {
      return 'MANAGER';
    }
    return 'USER';
  }
}

