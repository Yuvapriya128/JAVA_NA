import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStateService } from './auth-state.service';

export const roleGuard = (allowedRoles: string[]): CanActivateFn => {
  return (route, state) => {
    const authState = inject(AuthStateService);
    const router = inject(Router);

    const currentUser = authState.currentUser();
    const role = (currentUser?.role ?? '').trim().toUpperCase();
    const isAuthenticated = authState.isAuthenticated();
    const normalizedAllowed = allowedRoles.map((item) => (item ?? '').trim().toUpperCase());

    console.log('[RoleGuard] URL:', state.url);
    console.log('[RoleGuard] currentUser:', currentUser);
    console.log('[RoleGuard] role:', role);
    console.log('[RoleGuard] allowedRoles:', allowedRoles);
    console.log('[RoleGuard] isAuthenticated:', isAuthenticated);

    if (!isAuthenticated) {
      console.warn('[RoleGuard] blocked (not authenticated) -> /auth/login');
      return router.createUrlTree(['/auth/login']);
    }

    const hasRole = normalizedAllowed.includes(role);
    console.log('[RoleGuard] decision:', hasRole ? 'ALLOW' : 'BLOCK');

    if (hasRole) {
      return true;
    }

    console.warn('[RoleGuard] blocked (role mismatch) -> /auth/login');
    return router.createUrlTree(['/auth/login']);
  };
};
