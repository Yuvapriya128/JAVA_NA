import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ToastNotificationService } from '../../../../services/shared/toast-notification.service';

@Component({
  selector: 'app-toast-container',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-container.component.html',
  styleUrls: ['./toast-container.component.css']
})
export class ToastContainerComponent {
  readonly toastService = inject(ToastNotificationService);

  dismiss(id: number): void {
    this.toastService.dismiss(id);
  }

  iconClass(variant: 'success' | 'danger' | 'warning' | 'info'): string {
    if (variant === 'success') {
      return 'bi bi-check-circle-fill';
    }
    if (variant === 'danger') {
      return 'bi bi-x-circle-fill';
    }
    if (variant === 'warning') {
      return 'bi bi-exclamation-triangle-fill';
    }
    return 'bi bi-info-circle-fill';
  }
}
