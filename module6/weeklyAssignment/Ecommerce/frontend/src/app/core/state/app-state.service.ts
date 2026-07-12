import { Injectable } from '@angular/core';
import { signal, computed, WritableSignal } from '@angular/core';

export interface AppState {
  isLoading: boolean;
  notification: {
    message: string;
    type: 'success' | 'error' | 'info' | 'warning';
  } | null;
  sidebarOpen: boolean;
  theme: 'light' | 'dark';
}

/**
 * Global Application State Management using Angular Signals
 * Provides centralized state for application-wide concerns
 */
@Injectable({
  providedIn: 'root'
})
export class AppStateService {
  // Writable signals for state
  private readonly isLoadingSignal: WritableSignal<boolean> = signal(false);
  private readonly notificationSignal: WritableSignal<AppState['notification']> = signal(null);
  private readonly sidebarOpenSignal: WritableSignal<boolean> = signal(true);
  private readonly themeSignal: WritableSignal<'light' | 'dark'> = signal('light');

  // Readonly signals for consumers
  readonly isLoading = this.isLoadingSignal.asReadonly();
  readonly notification = this.notificationSignal.asReadonly();
  readonly sidebarOpen = this.sidebarOpenSignal.asReadonly();
  readonly theme = this.themeSignal.asReadonly();

  // Computed signals
  readonly state = computed<AppState>(() => ({
    isLoading: this.isLoadingSignal(),
    notification: this.notificationSignal(),
    sidebarOpen: this.sidebarOpenSignal(),
    theme: this.themeSignal()
  }));

  // State mutations
  setLoading(loading: boolean): void {
    this.isLoadingSignal.set(loading);
  }

  showNotification(message: string, type: 'success' | 'error' | 'info' | 'warning' = 'info'): void {
    this.notificationSignal.set({ message, type });
    // Auto-clear after 5 seconds
    setTimeout(() => this.clearNotification(), 5000);
  }

  clearNotification(): void {
    this.notificationSignal.set(null);
  }

  toggleSidebar(): void {
    this.sidebarOpenSignal.update(value => !value);
  }

  setSidebarOpen(open: boolean): void {
    this.sidebarOpenSignal.set(open);
  }

  setTheme(theme: 'light' | 'dark'): void {
    this.themeSignal.set(theme);
  }

  toggleTheme(): void {
    this.themeSignal.update(current => current === 'light' ? 'dark' : 'light');
  }
}

