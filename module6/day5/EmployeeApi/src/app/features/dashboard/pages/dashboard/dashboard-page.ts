import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import EmployeeResponseDTO from '../../../../core/dto/EmployeeResponseDTO';
import { CrudStatus } from '../../../../core/dto/CrudStatus';
import { EmployeeService } from '../../../../core/services/employee.service';
import { PageHeader } from '../../../../shared/page-header/page-header';
import { Loading } from '../../../../shared/loading/loading';
import { Alert } from '../../../../shared/alert/alert';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [RouterLink, PageHeader, Loading, Alert, DecimalPipe],
  template: `
    <app-page-header title="Dashboard" subtitle="Welcome back!" icon="bi-speedometer2"></app-page-header>

    <div class="row g-3 mb-4">
      <div class="col-12 col-md-4">
        <a class="card text-decoration-none shadow-sm h-100" routerLink="/employees/add">
          <div class="card-body d-flex justify-content-between align-items-center">
            <div>
              <div class="small text-muted">Quick Action</div>
              <div class="fw-semibold">Add Employee</div>
            </div>
            <i class="bi bi-person-plus fs-4 text-primary" aria-hidden="true"></i>
          </div>
        </a>
      </div>
      <div class="col-12 col-md-4">
        <a class="card text-decoration-none shadow-sm h-100" routerLink="/employees">
          <div class="card-body d-flex justify-content-between align-items-center">
            <div>
              <div class="small text-muted">Quick Action</div>
              <div class="fw-semibold">View Employees</div>
            </div>
            <i class="bi bi-people fs-4 text-primary" aria-hidden="true"></i>
          </div>
        </a>
      </div>
      <div class="col-12 col-md-4">
        <a class="card text-decoration-none shadow-sm h-100" routerLink="/employees">
          <div class="card-body d-flex justify-content-between align-items-center">
            <div>
              <div class="small text-muted">Quick Action</div>
              <div class="fw-semibold">Search Employee</div>
            </div>
            <i class="bi bi-search fs-4 text-primary" aria-hidden="true"></i>
          </div>
        </a>
      </div>
    </div>

    @if (status().loading) {
      <app-loading message="Loading dashboard metrics..."></app-loading>
    }

    @if (status().error) {
      <app-alert type="danger" [message]="status().error || ''"></app-alert>
    }

    <div class="row g-3 mb-4">
      <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div class="text-muted small"><i class="bi bi-people me-1" aria-hidden="true"></i>Total</div>
            <div class="h4 mb-0">{{ totalEmployees() }}</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div class="text-muted small"><i class="bi bi-graph-up-arrow me-1" aria-hidden="true"></i>Highest Salary</div>
            <div class="h4 mb-0">{{ highestSalary() | number }}</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div class="text-muted small"><i class="bi bi-graph-down-arrow me-1" aria-hidden="true"></i>Lowest Salary</div>
            <div class="h4 mb-0">{{ lowestSalary() | number }}</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-lg-3">
        <div class="card shadow-sm h-100">
          <div class="card-body">
            <div class="text-muted small"><i class="bi bi-calculator me-1" aria-hidden="true"></i>Average Salary</div>
            <div class="h4 mb-0">{{ averageSalary() | number:'1.0-2' }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="card shadow-sm">
      <div class="card-header bg-white d-flex align-items-center justify-content-between">
        <span class="fw-semibold"><i class="bi bi-clock-history me-2" aria-hidden="true"></i>Latest Employees</span>
        <span class="badge text-bg-light">{{ latestEmployees().length }} records</span>
      </div>
      <div class="table-responsive">
        <table class="table align-middle mb-0">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Salary</th>
            </tr>
          </thead>
          <tbody>
            @for (employee of latestEmployees(); track employee.id) {
              <tr>
                <td>{{ employee.id }}</td>
                <td>{{ employee.name }}</td>
                <td>{{ employee.salary | number }}</td>
              </tr>
            } @empty {
              <tr><td colspan="3" class="text-center text-muted py-3">No employees found.</td></tr>
            }
          </tbody>
        </table>
      </div>
    </div>
  `
})
export class DashboardPage implements OnInit {
  private readonly employeeService = inject(EmployeeService);

  readonly status = signal<CrudStatus>({ loading: false });
  readonly employees = signal<EmployeeResponseDTO[]>([]);

  readonly totalEmployees = computed(() => this.employees().length);
  readonly highestSalary = computed(() => this.employees().length ? Math.max(...this.employees().map((employee) => employee.salary)) : 0);
  readonly lowestSalary = computed(() => this.employees().length ? Math.min(...this.employees().map((employee) => employee.salary)) : 0);
  readonly averageSalary = computed(() => {
    const all = this.employees();
    if (!all.length) {
      return 0;
    }

    const total = all.reduce((sum, employee) => sum + employee.salary, 0);
    return total / all.length;
  });

  readonly latestEmployees = computed(() => [...this.employees()].sort((a, b) => b.id - a.id).slice(0, 5));

  ngOnInit(): void {
    this.status.set({ loading: true });
    this.employeeService.getAll().subscribe({
      next: (data) => {
        this.employees.set(data);
        this.status.set({ loading: false, success: true });
      },
      error: (error: HttpErrorResponse) => {
        this.status.set({ loading: false, error: this.errorMessage(error) });
      }
    });
  }

  private errorMessage(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'Backend is unreachable on port 8080.';
    }
    return error.error?.message || error.message || 'Failed to load dashboard data.';
  }
}
