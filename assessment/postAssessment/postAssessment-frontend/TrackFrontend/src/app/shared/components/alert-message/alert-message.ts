import { Component, inject } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-alert-message',
  templateUrl: './alert-message.html',
  styleUrl: './alert-message.css',
})
export class AlertMessage {
  protected readonly notifications = inject(NotificationService);
}
