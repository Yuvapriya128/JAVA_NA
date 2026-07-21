import { Injectable, signal } from '@angular/core';

export type ConfirmationVariant = 'danger' | 'warning' | 'info';

export interface ConfirmationDialogState {
  visible: boolean;
  title: string;
  message: string;
  confirmText: string;
  cancelText: string;
  variant: ConfirmationVariant;
}

const DEFAULT_STATE: ConfirmationDialogState = {
  visible: false,
  title: '',
  message: '',
  confirmText: 'Confirm',
  cancelText: 'Cancel',
  variant: 'warning'
};

@Injectable({
  providedIn: 'root'
})
export class ConfirmationDialogService {
  readonly state = signal<ConfirmationDialogState>(DEFAULT_STATE);
  private resolver: ((value: boolean) => void) | null = null;

  confirm(options: Partial<Omit<ConfirmationDialogState, 'visible'>> & { message: string }): Promise<boolean> {
    if (this.resolver) {
      this.resolver(false);
      this.resolver = null;
    }

    this.state.set({
      visible: true,
      title: options.title ?? 'Please Confirm',
      message: options.message,
      confirmText: options.confirmText ?? 'Confirm',
      cancelText: options.cancelText ?? 'Cancel',
      variant: options.variant ?? 'warning'
    });

    return new Promise<boolean>((resolve) => {
      this.resolver = resolve;
    });
  }

  approve(): void {
    this.finish(true);
  }

  cancel(): void {
    this.finish(false);
  }

  private finish(approved: boolean): void {
    this.state.set(DEFAULT_STATE);
    this.resolver?.(approved);
    this.resolver = null;
  }
}
