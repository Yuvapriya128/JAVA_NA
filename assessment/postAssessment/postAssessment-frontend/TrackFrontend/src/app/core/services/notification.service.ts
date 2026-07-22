import { Injectable, signal } from '@angular/core';

export type NotificationType = 'success' | 'error' | 'info';

export interface Notification {
  id: number;
  type: NotificationType;
  message: string;
}

/**
 * Central store for user-facing feedback messages.
 * Components read the `notifications` signal to render alerts/toasts.
 */
@Injectable({ providedIn: 'root' })
export class NotificationService {
  private nextId = 0;
  private readonly _notifications = signal<Notification[]>([]);

  /** Read-only view of the current notifications. */
  readonly notifications = this._notifications.asReadonly();

  success(message: string): void {
    this.push('success', message);
  }

  error(message: string): void {
    this.push('error', message);
  }

  info(message: string): void {
    this.push('info', message);
  }

  dismiss(id: number): void {
    this._notifications.update((list) => list.filter((n) => n.id !== id));
  }

  clear(): void {
    this._notifications.set([]);
  }

  private push(type: NotificationType, message: string): void {
    const notification: Notification = { id: ++this.nextId, type, message };
    this._notifications.update((list) => [...list, notification]);
    // Auto-dismiss after 5 seconds.
    setTimeout(() => this.dismiss(notification.id), 5000);
  }
}
