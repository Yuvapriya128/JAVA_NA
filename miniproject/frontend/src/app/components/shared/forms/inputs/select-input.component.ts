import { Component, EventEmitter, Input, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-select-input',
  standalone: true,
  imports: [FormsModule],
  templateUrl: './select-input.component.html',
  styleUrls: ['./select-input.component.css'],
})
export class SelectInputComponent {
  @Input({ required: true }) id = '';
  @Input({ required: true }) label = '';
  @Input() options: string[] = [];
  @Input() colClass = 'col-md-6';
  @Input() model = '';
  @Output() modelChange = new EventEmitter<string>();
}
