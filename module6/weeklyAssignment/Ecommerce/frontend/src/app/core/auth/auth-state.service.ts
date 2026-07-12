import { Injectable, computed, signal } from '@angular/core';
import { ROLE_PERMISSIONS, type Permission } from './constants/permissions.constants';
import { ROLES } from './constants/roles.constants';

export interface AuthUser {
  customerId: number | null;
  email: string;
  name: string;
  role: string;
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  private readonly currentUserSignal = signal<AuthUser | null>(null);
  private readonly realRoleSignal = signal<string>('');
  private readonly effectiveRoleSignal = signal<string>('');
  private readonly permissionsSignal = signal<string[]>([]);

  readonly currentUser = this.currentUserSignal.asReadonly();
  readonly token = computed(() => this.currentUserSignal()?.token ?? '');
  readonly realRole = this.realRoleSignal.asReadonly();
  readonly effectiveRole = this.effectiveRoleSignal.asReadonly();
  readonly permissions = this.permissionsSignal.asReadonly();

  readonly backendRole = this.realRoleSignal.asReadonly();
  readonly activeUiRole = this.effectiveRoleSignal.asReadonly();

  readonly isLoggedIn = computed(() => !!this.token());
  readonly isAuthenticated = computed(() => this.isLoggedIn());

  setCurrentUser(user: AuthUser | null): void {
    const normalizedUser = user
      ? {
          ...user,
          role: this.normalizeRole(user.role),
          email: (user.email ?? '').trim(),
          name: (user.name ?? '').trim()
        }
      : null;

    const resolvedRealRole = this.normalizeRole(normalizedUser?.role ?? '');

    this.currentUserSignal.set(normalizedUser);
    this.realRoleSignal.set(resolvedRealRole);
    this.effectiveRoleSignal.set(resolvedRealRole);
    this.permissionsSignal.set(this.resolvePermissions(resolvedRealRole));

    console.log('[AuthState] setCurrentUser');
    console.log('[AuthState] tokenExists:', !!this.token());
    console.log('[AuthState] user:', this.currentUserSignal());
    console.log('[AuthState] realRole:', this.realRoleSignal());
    console.log('[AuthState] effectiveRole:', this.effectiveRoleSignal());
    console.log('[AuthState] permissions:', this.permissionsSignal());
  }

  setSelectedUiRole(role: string): void {
    const normalized = this.normalizeRole(role || '');
    this.effectiveRoleSignal.set(this.resolveEffectiveRole(this.realRoleSignal(), normalized));
    this.permissionsSignal.set(this.resolvePermissions(this.effectiveRoleSignal()));
  }

  switchToUserView(): boolean {
    if (this.realRoleSignal() !== ROLES.ADMIN) {
      return false;
    }

    this.setSelectedUiRole(ROLES.USER);
    return true;
  }

  switchToAdminView(): boolean {
    if (this.realRoleSignal() !== ROLES.ADMIN) {
      return false;
    }

    this.setSelectedUiRole('');
    return true;
  }

  isAdmin(): boolean {
    return this.effectiveRoleSignal() === ROLES.ADMIN;
  }

  isManager(): boolean {
    return this.effectiveRoleSignal() === ROLES.MANAGER;
  }

  isUser(): boolean {
    return this.effectiveRoleSignal() === ROLES.USER;
  }

  hasRole(role: string): boolean {
    return this.effectiveRoleSignal() === role;
  }

  hasPermission(permission: string): boolean {
    return this.permissionsSignal().includes(permission);
  }

  reset(): void {
    this.currentUserSignal.set(null);
    this.realRoleSignal.set('');
    this.effectiveRoleSignal.set('');
    this.permissionsSignal.set([]);
  }

  private resolveEffectiveRole(realRole: string, requestedRole: string): string {
    if (realRole === ROLES.ADMIN && requestedRole === ROLES.USER) {
      return ROLES.USER;
    }

    return realRole;
  }

  private resolvePermissions(role: string): Permission[] {
    return ROLE_PERMISSIONS[role] ?? [];
  }

  private normalizeRole(role: string): string {
    const normalized = (role || '').trim().toUpperCase();
    if (!normalized) {
      return '';
    }

    if (normalized.startsWith('ROLE_')) {
      return normalized.replace('ROLE_', '');
    }

    return normalized;
  }
}
