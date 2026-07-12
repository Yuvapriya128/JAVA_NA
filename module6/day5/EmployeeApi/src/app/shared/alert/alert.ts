import { Component, input } from '@angular/core';

@Component({
  selector: 'app-alert',
  standalone: true,
  template: `
    @if (message()) {
      <div class="alert d-flex align-items-start gap-2" [class.alert-danger]="type() === 'danger'" [class.alert-success]="type() === 'success'" [class.alert-info]="type() === 'info'" role="alert">
        <i [class]="'bi ' + iconClass()" aria-hidden="true"></i>
        <span>{{ message() }}</span>
      </div>
    }
  `
})
export class Alert {
  readonly type = input<'success' | 'danger' | 'info'>('info');
  readonly message = input<string>('');

  iconClass(): string {
    if (this.type() === 'danger') {
      return 'bi-exclamation-triangle-fill';
    }
    if (this.type() === 'success') {
      return 'bi-check-circle-fill';
    }
    return 'bi-info-circle-fill';
  }
}

