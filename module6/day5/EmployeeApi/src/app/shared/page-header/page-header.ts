import { Component, input } from '@angular/core';

@Component({
  selector: 'app-page-header',
  standalone: true,
  template: `
    <div class="mb-4 pb-2 border-bottom">
      <h1 class="h3 mb-1 d-flex align-items-center gap-2">
        @if (icon()) {
          <i [class]="'bi ' + icon() + ' text-primary'" aria-hidden="true"></i>
        }
        <span>{{ title() }}</span>
      </h1>
      @if (subtitle()) {
        <p class="text-muted mb-0">{{ subtitle() }}</p>
      }
    </div>
  `
})
export class PageHeader {
  readonly title = input.required<string>();
  readonly subtitle = input<string>('');
  readonly icon = input<string>('');
}

