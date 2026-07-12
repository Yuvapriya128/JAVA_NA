import { Injectable } from '@angular/core';

export interface JwtPayload {
  exp?: number;
  iat?: number;
  sub?: string;
  role?: string;
  customerId?: number | string;
  email?: string;
  name?: string;
  [key: string]: unknown;
}

@Injectable({
  providedIn: 'root'
})
export class JwtService {
  decodeToken(token: string): JwtPayload | null {
    if (!token) {
      return null;
    }

    const parts = token.split('.');
    if (parts.length !== 3) {
      return null;
    }

    try {
      const payload = JSON.parse(this.decodeBase64Url(parts[1])) as JwtPayload;
      return payload;
    } catch {
      return null;
    }
  }

  getRole(token: string): string {
    const payload = this.decodeToken(token);
    return this.normalizeRole(payload?.role);
  }

  getCustomerId(token: string): number | null {
    const payload = this.decodeToken(token);
    const value = Number(payload?.customerId);
    return Number.isFinite(value) && value > 0 ? value : null;
  }

  getEmail(token: string): string {
    const payload = this.decodeToken(token);
    return typeof payload?.email === 'string' ? payload.email : '';
  }

  getName(token: string): string {
    const payload = this.decodeToken(token);
    if (typeof payload?.name === 'string' && payload.name.trim()) {
      return payload.name;
    }

    if (typeof payload?.sub === 'string') {
      return payload.sub;
    }

    return '';
  }

  isExpired(token: string): boolean {
    const payload = this.decodeToken(token);
    if (!payload?.exp) {
      return false;
    }

    const nowInSeconds = Math.floor(Date.now() / 1000);
    return payload.exp <= nowInSeconds;
  }


  private normalizeRole(value: unknown): string {
    if (value == null) {
      return '';
    }

    const raw = `${value}`.trim().toUpperCase();
    if (!raw) {
      return '';
    }

    if (raw.startsWith('ROLE_')) {
      return raw.replace('ROLE_', '');
    }

    if (raw.startsWith('SCOPE_')) {
      return raw.replace('SCOPE_', '');
    }

    return raw;
  }

  private decodeBase64Url(value: string): string {
    const normalized = value.replace(/-/g, '+').replace(/_/g, '/');
    const padded = normalized.padEnd(Math.ceil(normalized.length / 4) * 4, '=');
    return globalThis.atob(padded);
  }
}
