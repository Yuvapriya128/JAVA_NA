import { Component, inject, input, OnInit, signal } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';
import { EmployeeService } from '../../../../core/services/employee.service';
import EmployeeResponseDTO from '../../../../core/dto/EmployeeResponseDTO';
import { CrudStatus } from '../../../../core/dto/CrudStatus';
import { Alert } from '../../../../shared/alert/alert';
import { Loading } from '../../../../shared/loading/loading';
import { PageHeader } from '../../../../shared/page-header/page-header';
import { DecimalPipe } from '@angular/common';

@Component({
  selector: 'app-emp-detail',
  standalone: true,
  imports: [Alert, Loading, PageHeader, DecimalPipe],
  template: `
    <app-page-header title="Employee Details" subtitle="View employee information." icon="bi-person-vcard"></app-page-header>

    @if (status().loading) {
      <app-loading message="Loading employee details..."></app-loading>
    }

    @if (status().error) {
      <app-alert type="danger" [message]="status().error || ''"></app-alert>
    }

    @if (employee()) {
      <div class="card shadow-sm">
        <div class="card-body">
          <h5 class="card-title d-flex align-items-center gap-2 mb-3">
            <i class="bi bi-person-badge text-primary" aria-hidden="true"></i>
            <span>{{ employee()?.name }}</span>
          </h5>
          <p class="mb-2 d-flex align-items-center gap-2">
            <i class="bi bi-hash text-muted" aria-hidden="true"></i>
            <strong>ID:</strong> {{ employee()?.id }}
          </p>
          <p class="mb-0 d-flex align-items-center gap-2">
            <i class="bi bi-cash-coin text-muted" aria-hidden="true"></i>
            <strong>Salary:</strong> {{ employee()?.salary | number }}
          </p>
        </div>
      </div>
    }
  `
})
export class EmpDetail implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly employeeService = inject(EmployeeService);

  readonly employeeInput = input<EmployeeResponseDTO | null>(null);
  readonly employee = signal<EmployeeResponseDTO | null>(null);
  readonly status = signal<CrudStatus>({ loading: false });

  ngOnInit(): void {
    const inputEmployee = this.employeeInput();
    if (inputEmployee) {
      this.employee.set(inputEmployee);
      return;
    }

    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (id > 0) {
      this.loadById(id);
    }
  }

  private loadById(id: number): void {
    this.status.set({ loading: true });
    this.employeeService.getById(id).subscribe({
      next: (data) => {
        this.employee.set(data);
        this.status.set({ loading: false, success: true });
      },
      error: (error: HttpErrorResponse) => {
        this.status.set({ loading: false, error: this.errorMessage(error, id) });
      }
    });
  }

  private errorMessage(error: HttpErrorResponse, id: number): string {
    if (error.status === 404) {
      return `Employee with ID ${id} not found.`;
    }
    if (error.status === 0) {
      return 'Backend is unreachable on port 8080.';
    }
    return error.error?.message || error.message || 'Failed to load employee.';
  }
}
