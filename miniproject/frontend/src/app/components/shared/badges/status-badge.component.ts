import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-status-badge',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './status-badge.component.html',
  styleUrls: ['./status-badge.component.css']})
export class StatusBadgeComponent {
  @Input() label = 'UNKNOWN';

  get statusClass(): string {
    const value = this.label.toUpperCase();
    if (['ACTIVE', 'PAID', 'SUCCESS', 'CLOSED', 'APPROVED'].includes(value)) {
      return 'bg-success-subtle';
    }
    if (['OVERDUE', 'FAILED', 'DEACTIVATED', 'REJECTED'].includes(value)) {
      return 'bg-danger-subtle';
    }
    if (['PARTIAL', 'PENDING', 'PENDING APPROVAL', 'UNDER_REVIEW', 'IN_REVIEW'].includes(value)) {
      return 'bg-warning-subtle';
    }
    if (['USER', 'MANAGER', 'ADMIN'].includes(value)) {
      return 'bg-primary-subtle';
    }
    return 'bg-secondary-subtle';
  }
}
