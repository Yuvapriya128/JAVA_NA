import { Injectable, inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { PLATFORM_ID } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private readonly platformId = inject(PLATFORM_ID);
  private readonly isBrowser = isPlatformBrowser(this.platformId);

  private readonly tokenKey = 'auth_token';

  getToken(): string {
    return this.read(this.tokenKey) ?? '';
  }

  setToken(token: string): void {
    this.write(this.tokenKey, token);
  }

  clearToken(): void {
    this.remove(this.tokenKey);
  }

  clearAll(): void {
    this.clearToken();
  }

  private read(key: string): string | null {
    const storage = this.getStorage();
    if (!storage) {
      return null;
    }

    try {
      return storage.getItem(key);
    } catch {
      return null;
    }
  }

  private write(key: string, value: string): void {
    const storage = this.getStorage();
    if (!storage) {
      return;
    }

    try {
      storage.setItem(key, value);
    } catch {
      // Ignore browser storage errors (private mode/quota).
    }
  }

  private remove(key: string): void {
    const storage = this.getStorage();
    if (!storage) {
      return;
    }

    try {
      storage.removeItem(key);
    } catch {
      // Ignore browser storage errors.
    }
  }

  private getStorage(): Storage | null {
    if (!this.isBrowser || typeof globalThis.sessionStorage === 'undefined') {
      return null;
    }

    return globalThis.sessionStorage;
  }
}

