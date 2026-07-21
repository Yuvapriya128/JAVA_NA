import { DOCUMENT, isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID, inject, Injectable, signal } from '@angular/core';

type ThemeMode = 'light' | 'dark';

@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private readonly document = inject(DOCUMENT);
  private readonly platformId = inject(PLATFORM_ID);
  private readonly storageKey = 'loanhub-theme';

  readonly theme = signal<ThemeMode>('light');

  initializeTheme(): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    const stored = localStorage.getItem(this.storageKey);
    const canReadMediaPreference = typeof window.matchMedia === 'function';
    const preferred = stored === 'dark' || stored === 'light'
      ? stored
      : (canReadMediaPreference && window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light');

    this.applyTheme(preferred);
  }

  toggleTheme(): void {
    this.applyTheme(this.theme() === 'dark' ? 'light' : 'dark');
  }

  private applyTheme(theme: ThemeMode): void {
    if (!isPlatformBrowser(this.platformId)) {
      return;
    }

    this.theme.set(theme);
    this.document.documentElement.setAttribute('data-theme', theme);
    localStorage.setItem(this.storageKey, theme);
  }
}
