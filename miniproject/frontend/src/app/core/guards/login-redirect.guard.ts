import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenStorageService } from '../../services/auth/token-storage.service';

export const loginRedirectGuard: CanActivateFn = () => {
  const tokenStorage = inject(TokenStorageService);
  const router = inject(Router);

  if (tokenStorage.isLoggedIn()) {
    return router.createUrlTree(['/dashboard']);
  }

  return true;
};
