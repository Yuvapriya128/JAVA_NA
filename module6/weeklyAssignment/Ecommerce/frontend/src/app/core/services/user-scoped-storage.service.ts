import { Injectable, inject } from '@angular/core';
import { AuthStateService } from '../auth/auth-state.service';

@Injectable({
  providedIn: 'root'
})
export class UserScopedStorageService {
  private readonly authState = inject(AuthStateService);

  getCurrentUserStorageKey(prefix: string): string | null {
    const normalizedPrefix = (prefix ?? '').trim();
    if (!normalizedPrefix) {
      return null;
    }

    const user = this.authState.currentUser();
    const customerId = Number(user?.customerId);
    if (Number.isFinite(customerId) && customerId > 0) {
      return `${normalizedPrefix}_${customerId}`;
    }

    const normalizedEmail = (user?.email ?? '').trim().toLowerCase();
    if (normalizedEmail) {
      return `${normalizedPrefix}_${normalizedEmail}`;
    }

    return null;
  }
}
