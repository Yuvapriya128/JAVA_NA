import { Component, signal, viewChild } from '@angular/core';
import EmployeeResponseDTO from '../../../../core/dto/EmployeeResponseDTO';
import { PageHeader } from '../../../../shared/page-header/page-header';
import { EmpSearch } from '../../components/emp-search/emp-search';
import { EmpDetail } from '../../components/emp-detail/emp-detail';
import { EmpList } from '../../components/emp-list/emp-list';

@Component({
  selector: 'app-employees-page',
  standalone: true,
  imports: [PageHeader, EmpSearch, EmpDetail, EmpList],
  template: `
    <app-page-header title="Employee Management" subtitle="Manage all employee records." icon="bi-briefcase"></app-page-header>

    <app-emp-search (employeeFound)="onEmployeeFound($event)"></app-emp-search>

    @if (selectedEmployee()) {
      <div class="mb-4">
        <app-emp-detail [employeeInput]="selectedEmployee()"></app-emp-detail>
      </div>
    }

    <app-emp-list></app-emp-list>
  `
})
export class EmployeesPage {
  readonly selectedEmployee = signal<EmployeeResponseDTO | null>(null);
  readonly listRef = viewChild(EmpList);

  onEmployeeFound(employee: EmployeeResponseDTO | null): void {
    this.selectedEmployee.set(employee);
  }
}

