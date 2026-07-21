import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-validation-message',
  standalone: true,
  templateUrl: './validation-message.component.html',
  styleUrls: ['./validation-message.component.css'],
})
export class ValidationMessageComponent {
  @Input() message = 'Validation placeholder';
}

