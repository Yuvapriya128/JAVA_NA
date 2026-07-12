import { Component, input, output } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import EmployeeResponseDTO from '../../../../core/dto/EmployeeResponseDTO';

@Component({
  selector: 'app-employee-card',
  standalone: true,
  imports: [DecimalPipe],
  template: `
    <div class="card h-100 shadow-sm">
      <div class="card-body">
        <div class="d-flex justify-content-between align-items-center mb-2">
          <span class="badge text-bg-primary">
            <i class="bi bi-hash me-1" aria-hidden="true"></i>{{ employee().id }}
          </span>
          <span class="fw-semibold text-success">
            <i class="bi bi-currency-dollar me-1" aria-hidden="true"></i>{{ employee().salary | number }}
          </span>
        </div>
        <h5 class="card-title mb-3 d-flex align-items-center gap-2">
          <i class="bi bi-person-circle text-secondary" aria-hidden="true"></i>
          <span>{{ employee().name }}</span>
        </h5>
        <div class="d-flex gap-2">
          <button type="button" class="btn btn-sm btn-outline-primary" (click)="edit.emit(employee().id)">
            <i class="bi bi-pencil-square me-1" aria-hidden="true"></i>Edit
          </button>
          <button type="button" class="btn btn-sm btn-outline-danger" (click)="remove.emit(employee().id)">
            <i class="bi bi-trash me-1" aria-hidden="true"></i>Delete
          </button>
        </div>
      </div>
    </div>
  `
})
export class EmployeeCard {
  readonly employee = input.required<EmployeeResponseDTO>();
  readonly edit = output<number>();
  readonly remove = output<number>();
}
