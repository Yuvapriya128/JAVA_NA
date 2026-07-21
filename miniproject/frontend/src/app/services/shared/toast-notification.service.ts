import { Injectable, signal } from '@angular/core';

export type ToastVariant = 'success' | 'danger' | 'warning' | 'info';

export interface ToastOptions {
  timeoutMs?: number;
  actionLabel?: string;
  onAction?: () => void;
}

export interface ToastMessage {
  id: number;
  message: string;
  variant: ToastVariant;
  actionLabel?: string;
  onAction?: () => void;
}

@Injectable({
  providedIn: 'root'
})
export class ToastNotificationService {
  readonly messages = signal<ToastMessage[]>([]);
  private nextId = 1;
  private readonly dedupeWindowMs = 3500;
  private readonly recentToasts = new Map<string, number>();

  show(message: string, variant: ToastVariant = 'info', options: ToastOptions | number = 5000): void {
    const normalizedOptions: ToastOptions = typeof options === 'number' ? { timeoutMs: options } : options;
    const timeoutMs = normalizedOptions.timeoutMs ?? 5000;
    const dedupeKey = `${variant}:${message.trim()}`;
    const now = Date.now();
    const lastShownAt = this.recentToasts.get(dedupeKey);
    if (lastShownAt && now - lastShownAt < this.dedupeWindowMs) {
      return;
    }
    this.recentToasts.set(dedupeKey, now);
    const id = this.nextId++;
    this.messages.update((messages) => [
      ...messages,
      { id, message, variant, actionLabel: normalizedOptions.actionLabel, onAction: normalizedOptions.onAction }
    ]);

    if (timeoutMs > 0) {
      setTimeout(() => this.dismiss(id), timeoutMs);
    }
  }

  dismiss(id: number): void {
    this.messages.update((messages) => messages.filter((message) => message.id !== id));
  }

  runAction(id: number): void {
    const target = this.messages().find((message) => message.id === id);
    target?.onAction?.();
    this.dismiss(id);
  }

  clear(): void {
    this.messages.set([]);
  }
}
