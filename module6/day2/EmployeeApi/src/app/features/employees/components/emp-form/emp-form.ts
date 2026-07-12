import { Component, inject, input, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { EmployeeService } from '../../../../core/services/employee.service';
import EmployeeRequestDTO from '../../../../core/dto/EmployeeRequestDTO';
import { CrudStatus } from '../../../../core/dto/CrudStatus';
import { Alert } from '../../../../shared/alert/alert';
import { Loading } from '../../../../shared/loading/loading';
import { PageHeader } from '../../../../shared/page-header/page-header';

@Component({
  selector: 'app-emp-form',
  standalone: true,
  imports: [FormsModule, RouterLink, Alert, Loading, PageHeader],
  template: `
    <app-page-header [title]="headerTitle()" [subtitle]="headerSubtitle()" icon="bi-person-gear"></app-page-header>

    <div class="card shadow-sm mx-auto" style="max-width: 680px;">
      <div class="card-body">
        <div class="mb-3">
          <label for="employeeId" class="form-label">Employee ID</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-hash" aria-hidden="true"></i></span>
            <input
              id="employeeId"
              name="employeeId"
              class="form-control"
              type="number"
              min="1"
              [(ngModel)]="id"
              [readonly]="currentMode() === 'UPDATE'"
              [disabled]="currentMode() === 'ADD'"
            />
          </div>
        </div>

        <div class="mb-3">
          <label for="employeeName" class="form-label">Name</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-person" aria-hidden="true"></i></span>
            <input id="employeeName" name="employeeName" class="form-control" type="text" [(ngModel)]="name" />
          </div>
        </div>

        <div class="mb-3">
          <label for="employeeSalary" class="form-label">Salary</label>
          <div class="input-group">
            <span class="input-group-text"><i class="bi bi-currency-dollar" aria-hidden="true"></i></span>
            <input id="employeeSalary" name="employeeSalary" class="form-control" type="number" min="1" [(ngModel)]="salary" />
          </div>
        </div>

        <div class="d-flex gap-2 flex-wrap">
          <button class="btn btn-primary" type="button" (click)="submit()">
            <i class="bi bi-check2-circle me-1" aria-hidden="true"></i>{{ submitLabel() }}
          </button>
          <a class="btn btn-outline-secondary" routerLink="/employees">
            <i class="bi bi-arrow-left me-1" aria-hidden="true"></i>Back to Employees
          </a>
        </div>

        @if (status().loading) {
          <app-loading [message]="loadingMessage()"></app-loading>
        }
        @if (status().error) {
          <app-alert type="danger" [message]="status().error || ''"></app-alert>
        }
        @if (status().success && status().message) {
          <app-alert type="success" [message]="status().message || ''"></app-alert>
        }
      </div>
    </div>
  `
})
export class EmpForm implements OnInit {
  private readonly employeeService = inject(EmployeeService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly mode = input<'ADD' | 'UPDATE'>('ADD');
  readonly status = signal<CrudStatus>({ loading: false });

  id: number | null = null;
  name = '';
  salary: number | null = null;

  ngOnInit(): void {
    const routeMode = this.route.snapshot.data['mode'] as 'ADD' | 'UPDATE' | undefined;
    if (routeMode === 'UPDATE') {
      const id = Number(this.route.snapshot.paramMap.get('id'));
      if (id > 0) {
        this.id = id;
        this.currentMode.set('UPDATE');
        this.prefillEmployee(id);
      }
    }

    if (routeMode === 'ADD') {
      this.currentMode.set('ADD');
    }
  }

  readonly currentMode = signal<'ADD' | 'UPDATE'>('ADD');

  headerTitle(): string {
    return this.currentMode() === 'ADD' ? 'Add Employee' : 'Update Employee';
  }

  headerSubtitle(): string {
    return this.currentMode() === 'ADD'
      ? 'Create a new employee record.'
      : 'Update an existing employee record.';
  }

  submitLabel(): string {
    return this.currentMode() === 'ADD' ? 'Add Employee' : 'Update Employee';
  }

  loadingMessage(): string {
    return this.currentMode() === 'ADD' ? 'Adding employee...' : 'Updating employee...';
  }

  submit(): void {
    if (!this.validate()) {
      return;
    }

    const payload: EmployeeRequestDTO = {
      name: this.name.trim(),
      salary: this.salary as number
    };

    if (this.currentMode() === 'ADD') {
      this.create(payload);
      return;
    }

    this.edit(this.id as number, payload);
  }

  private validate(): boolean {
    if (this.currentMode() === 'UPDATE' && (!this.id || this.id <= 0)) {
      this.status.set({ loading: false, error: 'Employee ID must be greater than 0.' });
      return false;
    }

    if (!this.name.trim()) {
      this.status.set({ loading: false, error: 'Employee name is required.' });
      return false;
    }

    if (!this.salary || this.salary <= 0) {
      this.status.set({ loading: false, error: 'Salary must be greater than 0.' });
      return false;
    }

    return true;
  }

  private create(payload: EmployeeRequestDTO): void {
    this.status.set({ loading: true, error: '' });
    this.employeeService.add(payload).subscribe({
      next: () => {
        this.status.set({ loading: false, success: true, message: 'Employee added successfully.' });
        this.name = '';
        this.salary = null;
      },
      error: (error: HttpErrorResponse) => {
        this.status.set({ loading: false, error: this.errorMessage(error) });
      }
    });
  }

  private edit(id: number, payload: EmployeeRequestDTO): void {
    this.status.set({ loading: true, error: '' });
    this.employeeService.update(id, payload).subscribe({
      next: () => {
        this.status.set({ loading: false, success: true, message: 'Employee updated successfully.' });
        void this.router.navigate(['/employees']);
      },
      error: (error: HttpErrorResponse) => {
        this.status.set({ loading: false, error: this.errorMessage(error) });
      }
    });
  }

  private prefillEmployee(id: number): void {
    this.status.set({ loading: true, error: '' });
    this.employeeService.getById(id).subscribe({
      next: (employee) => {
        this.name = employee.name;
        this.salary = employee.salary;
        this.status.set({ loading: false });
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

