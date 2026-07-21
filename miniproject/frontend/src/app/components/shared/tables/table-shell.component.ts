import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-table-shell',
  standalone: true,
  templateUrl: './table-shell.component.html',
  styleUrls: ['./table-shell.component.css'],
})
export class TableShellComponent {
  @Input() title = 'Records';
}

