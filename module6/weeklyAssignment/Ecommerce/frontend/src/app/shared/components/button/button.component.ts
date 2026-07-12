import { Component, EventEmitter, Input, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-button',
  standalone: true,
  imports: [CommonModule],
  template: `
    <button
      class="btn"
      [type]="type"
      [ngClass]="buttonClasses"
      [disabled]="isLoading || disabled"
      (click)="buttonClick.emit()"
      [attr.aria-label]="ariaLabel || label">
      <span *ngIf="isLoading" class="spinner-border spinner-border-sm me-2" aria-hidden="true"></span>
      <i *ngIf="icon && !isLoading" [class]="'bi ' + icon + ' me-2'"></i>
      <span *ngIf="label">{{ label }}</span>
    </button>
  `,
  styles: [`
    :host {
      display: inline-block;
    }
  `]
})
export class ButtonComponent {
  @Input() label = '';
  @Input() variant: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'light' | 'dark' | 'outline-primary' | 'outline-secondary' = 'primary';
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() type: 'button' | 'submit' | 'reset' = 'button';
  @Input() icon?: string;
  @Input() isLoading = false;
  @Input() disabled = false;
  @Input() fullWidth = false;
  @Input() ariaLabel = '';
  @Output() buttonClick = new EventEmitter<void>();

  get buttonClasses(): string[] {
    const classes = [`btn-${this.variant}`];
    if (this.size === 'sm') classes.push('btn-sm');
    if (this.size === 'lg') classes.push('btn-lg');
    if (this.fullWidth) classes.push('w-100');
    return classes;
  }
}

