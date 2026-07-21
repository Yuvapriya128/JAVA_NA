import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { DashboardService } from '../../services/dashboard/dashboard.service';
import { PageHeaderComponent } from '../shared/page-header/page-header.component';
import { UiStatusState, defaultUiStatus } from '../../constants/ui-status';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, PageHeaderComponent],
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  private readonly dashboardService = inject(DashboardService);

  readonly stats = signal([
    { label: 'High Risk Accounts', value: '64' },
    { label: 'Manual Reviews Pending', value: '19' },
    { label: 'Suspended Customers', value: '12' },
    { label: 'Policy Alerts', value: '7' }
  ]);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.dashboardService.getAdminDashboard().subscribe({
      next: (response) => {
        this.stats.set([
          { label: 'High Risk Accounts', value: String(response.NPAAccounts) },
          { label: 'Manual Reviews Pending', value: String(response.overdueEMIs) },
          { label: 'Suspended Customers', value: String(response.closedLoans) },
          { label: 'Policy Alerts', value: String(response.totalLoans) }
        ]);
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
}
