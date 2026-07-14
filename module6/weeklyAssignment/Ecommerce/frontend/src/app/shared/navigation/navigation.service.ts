import { Injectable, computed, inject } from '@angular/core';
import { AuthStateService } from '../../core/auth/auth-state.service';
import { type Role } from '../../core/auth/constants/roles.constants';
import { CartService } from '../../core/services/cart.service';
import { APP_NAVIGATION_ITEMS, type AppNavigationItem } from './navigation.config';

@Injectable({
  providedIn: 'root'
})
export class NavigationService {
  private readonly authState = inject(AuthStateService);
  private readonly cartService = inject(CartService);

  readonly role = computed(() => this.authState.effectiveRole());

  readonly itemsForCurrentRole = computed(() => {
    const role = this.role();
    return APP_NAVIGATION_ITEMS
      .filter((item) => item.roles.includes(role as Role))
      .sort((a, b) => a.desktopPriority - b.desktopPriority);
  });

  readonly desktopItems = computed(() =>
    this.itemsForCurrentRole().filter((item) => item.showOnDesktop)
  );

  readonly mobileItems = computed(() =>
    [...this.itemsForCurrentRole()]
      .filter((item) => item.showOnMobile)
      .sort((a, b) => a.mobilePriority - b.mobilePriority)
  );

  readonly badgeCounts = computed<Record<string, number>>(() => ({
    cart: this.cartService.cartCount(),
    orders: 0,
    notifications: 0
  }));

  getBadgeCount(item: AppNavigationItem): number {
    const badgeKey = item.badgeKey;
    if (!badgeKey) {
      return 0;
    }

    const value = this.badgeCounts()[badgeKey];
    return Number.isFinite(value) && value > 0 ? value : 0;
  }
}
