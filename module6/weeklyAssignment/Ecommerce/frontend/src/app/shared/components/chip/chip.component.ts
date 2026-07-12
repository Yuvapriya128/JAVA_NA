import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-chip',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge rounded-pill d-inline-flex align-items-center gap-2" [ngClass]="'text-bg-' + variant">
      {{ label }}
      <button *ngIf="removable" type="button" class="btn-close btn-close-white" aria-label="Remove" (click)="remove.emit()"></button>
    </span>
  `,
  styles: [
    `
      .btn-close {
        font-size: 0.5rem;
      }
    `
  ]
})
export class ChipComponent {
  @Input() label = '';
  @Input() variant: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' = 'secondary';
  @Input() removable = false;

  @Output() remove = new EventEmitter<void>();
}

