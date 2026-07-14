import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { Injectable, PLATFORM_ID, inject } from '@angular/core';
import { signal, computed, WritableSignal, effect } from '@angular/core';

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
  private readonly document = inject(DOCUMENT);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);
  private readonly themeStorageKey = 'app_theme';

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

  constructor() {
    const initialTheme = this.resolveInitialTheme();
    this.themeSignal.set(initialTheme);
    this.applyTheme(initialTheme);

    effect(() => {
      const theme = this.themeSignal();
      this.applyTheme(theme);
      this.persistTheme(theme);
    });
  }

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

  private resolveInitialTheme(): 'light' | 'dark' {
    const storedTheme = this.readStoredTheme();
    if (storedTheme) {
      return storedTheme;
    }

    return this.prefersDarkMode() ? 'dark' : 'light';
  }

  private readStoredTheme(): 'light' | 'dark' | null {
    if (!this.isBrowser || typeof globalThis.localStorage === 'undefined') {
      return null;
    }

    try {
      const storedValue = globalThis.localStorage.getItem(this.themeStorageKey);
      return storedValue === 'dark' || storedValue === 'light' ? storedValue : null;
    } catch {
      return null;
    }
  }

  private persistTheme(theme: 'light' | 'dark'): void {
    if (!this.isBrowser || typeof globalThis.localStorage === 'undefined') {
      return;
    }

    try {
      globalThis.localStorage.setItem(this.themeStorageKey, theme);
    } catch {
      // Ignore storage persistence errors.
    }
  }

  private prefersDarkMode(): boolean {
    if (!this.isBrowser || typeof globalThis.matchMedia !== 'function') {
      return false;
    }

    return globalThis.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  private applyTheme(theme: 'light' | 'dark'): void {
    const rootElement = this.document?.documentElement;
    if (!rootElement) {
      return;
    }

    rootElement.setAttribute('data-theme', theme);
  }
}
