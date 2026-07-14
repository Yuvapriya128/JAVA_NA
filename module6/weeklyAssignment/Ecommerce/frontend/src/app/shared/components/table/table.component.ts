import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

export interface TableColumn {
  key: string;
  label: string;
  sortable?: boolean;
  headerClass?: string;
  cellClass?: string;
}

@Component({
  selector: 'app-table',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="table-responsive">
      <table class="table align-middle mb-0" [ngClass]="tableClasses">
        <thead>
          <tr>
            <th
              *ngFor="let column of columns"
              [ngClass]="column.headerClass"
              [class.cursor-pointer]="column.sortable"
              (click)="toggleSort(column)">
              <div class="d-inline-flex align-items-center gap-1">
                <span>{{ column.label }}</span>
                <i *ngIf="column.sortable" class="bi" [ngClass]="sortIcon(column)"></i>
              </div>
            </th>
          </tr>
        </thead>
        <tbody>
          <tr *ngIf="!rows?.length">
            <td [attr.colspan]="columns.length" class="text-center text-muted py-4">{{ emptyText }}</td>
          </tr>
          <tr *ngFor="let row of rows" (click)="rowClick.emit(row)" class="cursor-pointer">
            <td *ngFor="let column of columns" [ngClass]="column.cellClass">{{ readCell(row, column.key) }}</td>
          </tr>
        </tbody>
      </table>
    </div>
  `,
  styles: [
    `
      .cursor-pointer { cursor: pointer; }
    `
  ]
})
export class TableComponent {
  @Input() columns: TableColumn[] = [];
  @Input() rows: Record<string, unknown>[] = [];
  @Input() striped = true;
  @Input() hover = true;
  @Input() bordered = false;
  @Input() small = false;
  @Input() emptyText = 'No records found';

  @Output() rowClick = new EventEmitter<Record<string, unknown>>();
  @Output() sortChange = new EventEmitter<{ key: string; direction: 'asc' | 'desc' }>();

  sortedKey = '';
  sortDirection: 'asc' | 'desc' = 'asc';

  get tableClasses(): string[] {
    const classes: string[] = [];
    if (this.striped) classes.push('table-striped');
    if (this.hover) classes.push('table-hover');
    if (this.bordered) classes.push('table-bordered');
    if (this.small) classes.push('table-sm');
    return classes;
  }

  readCell(row: Record<string, unknown>, key: string): string {
    const value = row?.[key];
    if (value === null || value === undefined) {
      return '-';
    }
    return String(value);
  }

  toggleSort(column: TableColumn): void {
    if (!column.sortable) return;

    if (this.sortedKey === column.key) {
      this.sortDirection = this.sortDirection === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortedKey = column.key;
      this.sortDirection = 'asc';
    }

    this.sortChange.emit({ key: this.sortedKey, direction: this.sortDirection });
  }

  sortIcon(column: TableColumn): string {
    if (this.sortedKey !== column.key) return 'bi-arrow-down-up';
    return this.sortDirection === 'asc' ? 'bi-sort-down-alt' : 'bi-sort-up';
  }
}

