import { Component, EventEmitter, Input, Output } from '@angular/core';
import { SearchBarComponent } from '../../search-bar/search-bar.component';

@Component({
  selector: 'app-table-toolbar',
  standalone: true,
  imports: [SearchBarComponent],
  templateUrl: './table-toolbar.component.html',
  styleUrls: ['./table-toolbar.component.css'],
})
export class TableToolbarComponent {
  @Input() placeholder = 'Search records';
  @Input() term = '';
  @Output() termChange = new EventEmitter<string>();
  @Output() search = new EventEmitter<void>();
}

