import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="text-center py-5 px-3 border rounded bg-white">
      <i class="bi fs-1 text-muted" [ngClass]="icon"></i>
      <h5 class="mt-3 mb-2">{{ title }}</h5>
      <p class="text-muted mb-3">{{ description }}</p>
      <button *ngIf="actionText" class="btn btn-primary" type="button" (click)="actionClick.emit()">
        {{ actionText }}
      </button>
    </div>
  `
})
export class EmptyStateComponent {
  @Input() title = 'Nothing to show';
  @Input() description = 'Try changing filters or add new data.';
  @Input() icon = 'bi-inbox';
  @Input() actionText = '';
  @Output() actionClick = new EventEmitter<void>();
}

