import { Component, EventEmitter, Input, Output } from '@angular/core';
import { RouterLink } from '@angular/router';

export interface TableAction {
  key: string;
  icon: string;
  className: string;
  routerLink?: string;
  label?: string;
  tooltip?: string;
}

@Component({
  selector: 'app-table-action-buttons',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './table-action-buttons.component.html',
  styleUrls: ['./table-action-buttons.component.css'],
})
export class TableActionButtonsComponent {
  @Input() actions: TableAction[] = [];
  @Output() actionClick = new EventEmitter<string>();
}

