import { Injectable, signal } from '@angular/core';

export interface JwtPayload {
  sub?: string;
  exp?: number;
  iat?: number;
  roles?: string[] | string;
  role?: string;
  authorities?: string[];
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root'
})
export class TokenStorageService {
  private readonly tokenKey = 'loan_emi_auth_token';
  readonly isAuthenticated = signal(this.hasValidToken());

  saveToken(token: string): void {
    localStorage.setItem(this.tokenKey, token);
    this.isAuthenticated.set(this.hasValidToken());
  }

  getToken(): string | null {
    return localStorage.getItem(this.tokenKey);
  }

  removeToken(): void {
    localStorage.removeItem(this.tokenKey);
    this.isAuthenticated.set(false);
  }

  clearSession(): void {
    this.removeToken();
  }

  isLoggedIn(): boolean {
    return this.hasValidToken();
  }

  decodeTokenPayload(): JwtPayload | null {
    const token = this.getToken();
    if (!token) {
      return null;
    }

    const parts = token.split('.');
    if (parts.length < 2) {
      return null;
    }

    try {
      const payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const padded = payload.padEnd(payload.length + ((4 - (payload.length % 4)) % 4), '=');
      return JSON.parse(atob(padded)) as JwtPayload;
    } catch {
      return null;
    }
  }

  getUserRoles(): string[] {
    const payload = this.decodeTokenPayload();
    if (!payload) {
      return [];
    }

    const normalizedRoles = payload.roles ?? payload.role ?? payload.authorities;
    if (Array.isArray(normalizedRoles)) {
      return normalizedRoles.map((item) => String(item).toUpperCase());
    }

    if (typeof normalizedRoles === 'string') {
      return normalizedRoles
        .split(/[ ,]/)
        .map((item) => item.trim())
        .filter((item) => item.length > 0)
        .map((item) => item.toUpperCase());
    }

    return [];
  }

  hasRole(requiredRoles: string[]): boolean {
    if (requiredRoles.length === 0) {
      return true;
    }

    const userRoles = this.getUserRoles().map((role) => this.normalizeRole(role));
    const expectedRoles = requiredRoles.map((role) => this.normalizeRole(role));
    return expectedRoles.some((role) => userRoles.includes(role));
  }

  getPrimaryRole(): string {
    const [role] = this.getUserRoles().map((entry) => this.normalizeRole(entry));
    return role || 'USER';
  }

  isTokenExpired(): boolean {
    const payload = this.decodeTokenPayload();
    if (!payload?.exp) {
      return false;
    }

    return Date.now() >= payload.exp * 1000;
  }

  private hasValidToken(): boolean {
    const token = this.getToken();
    if (!token) {
      return false;
    }

    return !this.isTokenExpired();
  }

  private normalizeRole(role: string): string {
    const raw = String(role || '').trim().toUpperCase();
    if (raw.startsWith('ROLE_')) {
      return raw.slice(5);
    }
    return raw;
  }
}