import { CommonModule } from '@angular/common';
import { Component, EventEmitter, HostListener, Input, OnChanges, Output, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="show" class="toast-container position-fixed p-3" [ngClass]="positionClass">
      <div class="toast show align-items-center border-0 shadow" [ngClass]="toastClass" role="alert" aria-live="polite" aria-atomic="true">
        <div class="d-flex">
          <div class="toast-body">
            <div *ngIf="title" class="fw-semibold mb-1">{{ title }}</div>
            <div>{{ message }}</div>
          </div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" aria-label="Close" (click)="dismiss()"></button>
        </div>
      </div>
    </div>
  `,
  styles: [
    `
      .toast {
        animation: toastSlideIn 200ms ease-out;
      }

      @keyframes toastSlideIn {
        from {
          opacity: 0;
          transform: translateY(6px);
        }
        to {
          opacity: 1;
          transform: translateY(0);
        }
      }
    `
  ]
})
export class ToastComponent implements OnChanges {
  @Input() show = false;
  @Input() title = '';
  @Input() message = '';
  @Input() variant: 'success' | 'danger' | 'warning' | 'info' = 'info';
  @Input() position: 'top-end' | 'top-start' | 'bottom-end' | 'bottom-start' = 'top-end';
  @Input() autoHideMs = 2500;

  @Output() closed = new EventEmitter<void>();

  private timeoutId?: ReturnType<typeof setTimeout>;

  get toastClass(): string {
    return `text-bg-${this.variant}`;
  }

  get positionClass(): string {
    const mapping: Record<string, string> = {
      'top-end': 'top-0 end-0',
      'top-start': 'top-0 start-0',
      'bottom-end': 'bottom-0 end-0',
      'bottom-start': 'bottom-0 start-0'
    };
    return mapping[this.position] || mapping['top-end'];
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['show']?.currentValue) {
      this.startAutoHide();
    }
  }

  dismiss(): void {
    this.clearTimer();
    this.closed.emit();
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.show) {
      this.dismiss();
    }
  }

  private startAutoHide(): void {
    this.clearTimer();
    if (this.autoHideMs <= 0) return;
    this.timeoutId = setTimeout(() => this.dismiss(), this.autoHideMs);
  }

  private clearTimer(): void {
    if (!this.timeoutId) return;
    clearTimeout(this.timeoutId);
    this.timeoutId = undefined;
  }
}

