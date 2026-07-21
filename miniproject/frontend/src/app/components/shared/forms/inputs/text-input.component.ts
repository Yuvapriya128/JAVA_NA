import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-text-input',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './text-input.component.html',
  styleUrls: ['./text-input.component.css'],
})
export class TextInputComponent {
  @Input({ required: true }) id = '';
  @Input({ required: true }) label = '';
  @Input() placeholder = '';
  @Input() type = 'text';
  @Input() colClass = 'col-md-6';
  @Input() model: string | number = '';
  @Output() modelChange = new EventEmitter<string | number>();
}

