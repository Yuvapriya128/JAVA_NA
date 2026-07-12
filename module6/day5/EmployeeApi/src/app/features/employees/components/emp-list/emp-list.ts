import { Component, inject, OnInit, output, signal } from '@angular/core';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { EmployeeService } from '../../../../core/services/employee.service';
import EmployeeResponseDTO from '../../../../core/dto/EmployeeResponseDTO';
import { CrudStatus } from '../../../../core/dto/CrudStatus';
import { EmployeeCard } from '../employee-card/employee-card';
import { Alert } from '../../../../shared/alert/alert';
import { Loading } from '../../../../shared/loading/loading';

@Component({
  selector: 'app-emp-list',
  standalone: true,
  imports: [EmployeeCard, Alert, Loading],
  template: `
    <div class="card shadow-sm">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-3 flex-wrap gap-2">
          <h2 class="h5 mb-0 d-flex align-items-center gap-2">
            <i class="bi bi-people text-primary" aria-hidden="true"></i>
            <span>Employee List</span>
            <span class="badge rounded-pill text-bg-light">{{ employees().length }}</span>
          </h2>
          <div class="btn-group" role="group" aria-label="Sort options">
            <button class="btn btn-outline-secondary btn-sm" type="button" (click)="sortAsc()">
              <i class="bi bi-sort-up me-1" aria-hidden="true"></i>Salary
            </button>
            <button class="btn btn-outline-secondary btn-sm" type="button" (click)="sortDesc()">
              <i class="bi bi-sort-down me-1" aria-hidden="true"></i>Salary
            </button>
          </div>
        </div>

        @if (status().loading) {
          <app-loading message="Loading employees..."></app-loading>
        }

        @if (status().error) {
          <app-alert type="danger" [message]="status().error || ''"></app-alert>
        }

        <div class="row g-3">
          @for (employee of employees(); track employee.id) {
            <div class="col-12 col-md-6 col-xl-4">
              <app-employee-card [employee]="employee" (edit)="onEdit($event)" (remove)="deleteById($event)"></app-employee-card>
            </div>
          } @empty {
            @if (!status().loading) {
              <div class="col-12">
                <div class="alert alert-info mb-0 d-flex align-items-center gap-2">
                  <i class="bi bi-info-circle" aria-hidden="true"></i>
                  <span>No employees found.</span>
                </div>
              </div>
            }
          }
        </div>
      </div>
    </div>
  `
})
export class EmpList implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly router = inject(Router);

  readonly refreshed = output<void>();
  readonly status = signal<CrudStatus>({ loading: false });
  readonly employees = signal<EmployeeResponseDTO[]>([]);

  ngOnInit(): void {
    this.loadEmployees();
  }

  loadEmployees(): void {
    this.status.set({ loading: true, error: '' });
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

  sortAsc(): void {
    this.employees.update((list) => [...list].sort((a, b) => a.salary - b.salary));
  }

  sortDesc(): void {
    this.employees.update((list) => [...list].sort((a, b) => b.salary - a.salary));
  }

  onEdit(id: number): void {
    void this.router.navigate(['/employees/edit', id]);
  }

  deleteById(id: number): void {
    if (id <= 0) {
      this.status.set({ loading: false, error: 'Employee ID must be greater than 0.' });
      return;
    }

    this.status.set({ loading: true, error: '' });
    this.employeeService.delete(id).subscribe({
      next: () => {
        this.employees.update((list) => list.filter((employee) => employee.id !== id));
        this.status.set({ loading: false, success: true, message: 'Employee deleted successfully.' });
        this.refreshed.emit();
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
    if (error.status === 404) {
      return 'Employee not found.';
    }
    return error.error?.message || error.message || 'Request failed.';
  }
}

