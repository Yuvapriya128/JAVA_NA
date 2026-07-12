import { Component, inject, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { EmployeeService } from '../../../../core/services/employee.service';
import EmployeeResponseDTO from '../../../../core/dto/EmployeeResponseDTO';
import { Alert } from '../../../../shared/alert/alert';
import { Loading } from '../../../../shared/loading/loading';

@Component({
  selector: 'app-emp-search',
  standalone: true,
  imports: [FormsModule, Alert, Loading],
  template: `
    <div class="card shadow-sm mb-3">
      <div class="card-body">
        <h2 class="h6 mb-3 d-flex align-items-center gap-2">
          <i class="bi bi-search text-primary" aria-hidden="true"></i>
          <span>Search Employee</span>
        </h2>
        <div class="row g-2 align-items-end">
          <div class="col-md-8">
            <label for="searchId" class="form-label">Employee ID</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-hash" aria-hidden="true"></i></span>
              <input id="searchId" name="searchId" class="form-control" type="number" min="1" [(ngModel)]="id" />
            </div>
          </div>
          <div class="col-md-4">
            <button class="btn btn-primary w-100" type="button" (click)="search()">
              <i class="bi bi-search me-1" aria-hidden="true"></i>Search
            </button>
          </div>
        </div>

        @if (status().loading) {
          <app-loading message="Searching employee..."></app-loading>
        }
        @if (status().error) {
          <app-alert type="danger" [message]="status().error || ''"></app-alert>
        }
      </div>
    </div>
  `
})
export class EmpSearch {
  private readonly employeeService = inject(EmployeeService);

  readonly employeeFound = output<EmployeeResponseDTO | null>();
  readonly status = signal<{ loading: boolean; error?: string }>({ loading: false });

  id: number | null = null;

  search(): void {
    if (!this.id || this.id <= 0) {
      this.status.set({ loading: false, error: 'Employee ID must be greater than 0.' });
      this.employeeFound.emit(null);
      return;
    }

    this.status.set({ loading: true });
    this.employeeService.getById(this.id).subscribe({
      next: (employee) => {
        this.status.set({ loading: false });
        this.employeeFound.emit(employee);
      },
      error: (error: HttpErrorResponse) => {
        this.status.set({ loading: false, error: this.errorMessage(error) });
        this.employeeFound.emit(null);
      }
    });
  }

  private errorMessage(error: HttpErrorResponse): string {
    if (error.status === 404) {
      return `Employee with ID ${this.id} not found.`;
    }
    if (error.status === 0) {
      return 'Backend is unreachable on port 8080.';
    }
    return error.error?.message || error.message || 'Search failed.';
  }
}

