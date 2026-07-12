import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-rating',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="d-inline-flex align-items-center gap-1">
      <button
        *ngFor="let star of stars"
        type="button"
        class="btn btn-link p-0 text-warning text-decoration-none"
        [disabled]="readonly"
        (click)="setValue(star)">
        <i class="bi" [ngClass]="star <= value ? 'bi-star-fill' : 'bi-star'"></i>
      </button>
      <small *ngIf="showValue" class="text-muted ms-1">{{ value | number : '1.0-1' }}</small>
    </div>
  `
})
export class RatingComponent {
  @Input() value = 0;
  @Input() max = 5;
  @Input() readonly = true;
  @Input() showValue = true;

  @Output() valueChange = new EventEmitter<number>();

  get stars(): number[] {
    return Array.from({ length: this.max }, (_, index) => index + 1);
  }

  setValue(nextValue: number): void {
    if (this.readonly) return;
    this.value = nextValue;
    this.valueChange.emit(nextValue);
  }
}

