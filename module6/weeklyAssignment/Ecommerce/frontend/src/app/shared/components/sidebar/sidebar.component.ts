import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthStateService } from '../../../core/auth/auth-state.service';
import { ROLES } from '../../../core/auth/constants/roles.constants';
import { CartService } from '../../../core/services/cart.service';

interface MenuItem {
  label: string;
  route: string;
  icon: string;
  section: 'main' | 'account';
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SidebarComponent {
  private readonly authService = inject(AuthService);
  private readonly authState = inject(AuthStateService);
  private readonly cartService = inject(CartService);
  readonly roles = ROLES;

  readonly role = computed(() => this.authState.effectiveRole());
  readonly isViewingAsUser = computed(
    () => this.authState.realRole() === ROLES.ADMIN && this.authState.effectiveRole() === ROLES.USER
  );
  readonly cartBadgeCount = computed(() => this.cartService.cartCount());
  readonly userName = computed(() => this.authState.currentUser()?.name || 'User');
  readonly userEmail = computed(() => this.authState.currentUser()?.email || '');

  readonly menuItems = computed<MenuItem[]>(() => {
    const role = this.role();

    if (role === ROLES.ADMIN) {
      return [
        { label: 'Dashboard', route: '/dashboard', icon: 'bi-speedometer2', section: 'main' },
        { label: 'Customers', route: '/customers', icon: 'bi-people', section: 'main' },
        { label: 'Products', route: '/products', icon: 'bi-box', section: 'main' },
        { label: 'Orders', route: '/orders', icon: 'bi-cart3', section: 'main' },
        { label: 'Order Items', route: '/order-items', icon: 'bi-receipt', section: 'main' },
        { label: 'Profile', route: '/profile', icon: 'bi-person', section: 'account' }
      ];
    }

    if (role === ROLES.MANAGER) {
      return [
        { label: 'Dashboard', route: '/dashboard', icon: 'bi-speedometer2', section: 'main' },
        { label: 'Products', route: '/products', icon: 'bi-box', section: 'main' },
        { label: 'Orders', route: '/orders', icon: 'bi-cart3', section: 'main' },
        { label: 'Order Items', route: '/order-items', icon: 'bi-receipt', section: 'main' },
        { label: 'Profile', route: '/profile', icon: 'bi-person', section: 'account' }
      ];
    }

    return [
      { label: 'Products', route: '/products', icon: 'bi-box-seam', section: 'main' },
      { label: 'My Cart', route: '/cart', icon: 'bi-cart', section: 'main' },
      { label: 'Favorites', route: '/favorites', icon: 'bi-heart', section: 'main' },
      { label: 'My Orders', route: '/orders', icon: 'bi-bag-check', section: 'main' },
      { label: 'Profile', route: '/profile', icon: 'bi-person-circle', section: 'account' }
    ];
  });

  canSwitchToUserView(): boolean {
    return this.authState.realRole() === ROLES.ADMIN && this.authState.effectiveRole() === ROLES.ADMIN;
  }

  onViewAsUser(): void {
    this.authService.switchToUserView();
  }

  onReturnToAdmin(): void {
    this.authService.switchToAdminView();
  }

  onLogout(): void {
    this.authService.logout();
  }
}
