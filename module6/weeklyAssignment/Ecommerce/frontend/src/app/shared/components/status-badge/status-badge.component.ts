import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { BadgeComponent } from '../badge/badge.component';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule, BadgeComponent],
  template: `
    <app-badge [text]="label" [variant]="variant" [pill]="true"></app-badge>
  `
})
export class StatusBadgeComponent {
  @Input() status = 'pending';

  get label(): string {
    return this.status.replace(/[-_]/g, ' ').replace(/\b\w/g, char => char.toUpperCase());
  }

  get variant(): 'primary' | 'secondary' | 'success' | 'danger' | 'warning' | 'info' | 'light' | 'dark' {
    const normalized = this.status.toLowerCase();
    if (['delivered', 'paid', 'success', 'active', 'completed', 'in-stock'].includes(normalized)) return 'success';
    if (['cancelled', 'failed', 'rejected', 'inactive', 'out-of-stock'].includes(normalized)) return 'danger';
    if (['pending', 'processing', 'warning', 'hold'].includes(normalized)) return 'warning';
    if (['new', 'shipped', 'info'].includes(normalized)) return 'info';
    return 'secondary';
  }
}

