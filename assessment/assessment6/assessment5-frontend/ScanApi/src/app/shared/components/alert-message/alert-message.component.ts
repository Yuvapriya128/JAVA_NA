import { Component, input } from '@angular/core';

@Component({
  selector: 'app-alert-message',
  templateUrl: './alert-message.component.html',
})
export class AlertMessageComponent {
  readonly type = input<'success' | 'danger' | 'warning' | 'info'>('info');
  readonly message = input('');
}
