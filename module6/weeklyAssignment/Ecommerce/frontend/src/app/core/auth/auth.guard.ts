import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from './auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();
  const valid = authService.hasValidSessionToken();

  console.log('[AuthGuard] URL:', state.url);
  console.log('[AuthGuard] tokenExists:', !!token);
  console.log('[AuthGuard] tokenValid:', valid);

  if (valid) {
    return true;
  }

  console.warn('[AuthGuard] blocked -> /auth/login');
  return router.createUrlTree(['/auth/login']);
};
