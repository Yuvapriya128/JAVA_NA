import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-form-action-buttons',
  standalone: true,
  templateUrl: './form-action-buttons.component.html',
  styleUrls: ['./form-action-buttons.component.css'],
})
export class FormActionButtonsComponent {
  @Input() resetLabel = 'Reset';
  @Input() cancelLabel = 'Cancel';
  @Input() submitLabel = 'Submit';
  @Input() submitClass = 'btn btn-primary';
  @Input() loading = false;
  @Input() disableSubmit = false;
  @Output() resetClick = new EventEmitter<void>();
  @Output() cancelClick = new EventEmitter<void>();
  @Output() submitClick = new EventEmitter<void>();
}
