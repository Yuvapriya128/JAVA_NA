import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenStorageService } from '../../services/auth/token-storage.service';

export const roleGuard: CanActivateFn = (route) => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);
  const requiredRoles = (route.data?.['roles'] as string[] | undefined) ?? [];

  if (!tokenStorage.isLoggedIn()) {
    return router.createUrlTree(['/login']);
  }

  if (tokenStorage.hasRole(requiredRoles)) {
    return true;
  }

  return router.createUrlTree(['/access-denied']);
};
