import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-spinner',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="d-inline-flex align-items-center" [ngClass]="wrapperClass">
      <div class="spinner-border" [ngClass]="spinnerClass" role="status" [attr.aria-label]="label"></div>
      <span *ngIf="showLabel" class="ms-2">{{ label }}</span>
    </div>
  `,
  styles: [
    `
      .spinner-lg {
        width: 2rem;
        height: 2rem;
      }
    `
  ]
})
export class SpinnerComponent {
  @Input() size: 'sm' | 'md' | 'lg' = 'md';
  @Input() variant: 'primary' | 'secondary' | 'light' | 'dark' = 'primary';
  @Input() label = 'Loading';
  @Input() showLabel = false;
  @Input() wrapperClass = '';

  get spinnerClass(): string[] {
    const classes = [`text-${this.variant}`];
    if (this.size === 'sm') classes.push('spinner-border-sm');
    if (this.size === 'lg') classes.push('spinner-lg');
    return classes;
  }
}

