import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthStateService } from './auth-state.service';

export const permissionGuard = (requiredPermissions: string[]): CanActivateFn => {
  return (route, state) => {
    const authState = inject(AuthStateService);
    const router = inject(Router);

    const isAuthenticated = authState.isAuthenticated();
    const granted = authState.permissions();
    const hasPermission = requiredPermissions.every((permission) => authState.hasPermission(permission));

    console.log('[PermissionGuard] URL:', state.url);
    console.log('[PermissionGuard] requiredPermissions:', requiredPermissions);
    console.log('[PermissionGuard] grantedPermissions:', granted);
    console.log('[PermissionGuard] decision:', hasPermission ? 'ALLOW' : 'BLOCK');

    if (!isAuthenticated) {
      console.warn('[PermissionGuard] blocked (not authenticated) -> /auth/login');
      return router.createUrlTree(['/auth/login']);
    }

    if (!hasPermission) {
      console.warn('[PermissionGuard] blocked (permission mismatch) -> /auth/login');
      return router.createUrlTree(['/auth/login']);
    }

    return true;
  };
};
