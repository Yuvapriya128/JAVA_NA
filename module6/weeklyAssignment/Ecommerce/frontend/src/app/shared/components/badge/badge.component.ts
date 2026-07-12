import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-badge',
  standalone: true,
  imports: [CommonModule],
  template: `
    <span class="badge" [ngClass]="badgeClasses">{{ text }}</span>
  `
})
export class BadgeComponent {
  @Input() text = '';
  @Input() variant: 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'light' | 'dark' = 'secondary';
  @Input() pill = false;

  get badgeClasses(): string[] {
    const classes = [`text-bg-${this.variant}`];
    if (this.pill) classes.push('rounded-pill');
    return classes;
  }
}

