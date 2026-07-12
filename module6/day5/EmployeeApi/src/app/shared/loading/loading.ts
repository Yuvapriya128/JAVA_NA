import { Component, input } from '@angular/core';

@Component({
  selector: 'app-loading',
  standalone: true,
  template: `
    <div class="d-flex align-items-center gap-2 py-2 text-primary">
      <div class="spinner-border spinner-border-sm text-primary" role="status" aria-hidden="true"></div>
      <i class="bi bi-hourglass-split" aria-hidden="true"></i>
      <span>{{ message() }}</span>
    </div>
  `
})
export class Loading {
  readonly message = input<string>('Loading...');
}

