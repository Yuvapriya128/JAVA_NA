import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-search-bar',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './search-bar.component.html',
  styleUrls: ['./search-bar.component.css']})
export class SearchBarComponent {
  @Input() placeholder = 'Search records';
  @Input() term = '';
  @Output() termChange = new EventEmitter<string>();
  @Output() search = new EventEmitter<void>();
}

