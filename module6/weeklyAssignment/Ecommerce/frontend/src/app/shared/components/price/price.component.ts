import { CommonModule, CurrencyPipe } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-price',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  template: `
    <div class="d-flex align-items-center gap-2 flex-wrap">
      <span class="fw-bold" [ngClass]="sizeClass">{{ amount | currency : currencyCode : 'symbol' : '1.0-2' : locale }}</span>
      <span *ngIf="originalAmount" class="text-muted text-decoration-line-through small">
        {{ originalAmount | currency : currencyCode : 'symbol' : '1.0-2' : locale }}
      </span>
      <span *ngIf="discountPercentage > 0" class="badge text-bg-danger rounded-pill">-{{ discountPercentage }}%</span>
    </div>
  `
})
export class PriceComponent {
  @Input() amount = 0;
  @Input() originalAmount?: number;
  @Input() currencyCode = 'USD';
  @Input() locale = 'en-US';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';

  get discountPercentage(): number {
    if (!this.originalAmount || this.originalAmount <= this.amount) return 0;
    return Math.round(((this.originalAmount - this.amount) / this.originalAmount) * 100);
  }

  get sizeClass(): string {
    if (this.size === 'sm') return 'fs-6';
    if (this.size === 'lg') return 'fs-4';
    return 'fs-5';
  }
}

