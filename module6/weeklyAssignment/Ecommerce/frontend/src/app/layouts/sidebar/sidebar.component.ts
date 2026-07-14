import { Component, Input, Output, EventEmitter, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { AuthStateService } from '../../core/auth/auth-state.service';
import { ROLES } from '../../core/auth/constants/roles.constants';

interface MenuItem {
  label: string;
  route: string;
  icon?: string;
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  @Input() isOpen = true;
  @Output() itemClick = new EventEmitter<void>();

  private readonly authService = inject(AuthService);
  private readonly authState = inject(AuthStateService);
  readonly roles = ROLES;

  readonly role = computed(() => this.authState.effectiveRole());
  readonly isViewingAsUser = computed(
    () => this.authState.realRole() === ROLES.ADMIN && this.authState.effectiveRole() === ROLES.USER
  );

  readonly menuItems = computed<MenuItem[]>(() => {
    const role = this.role();

    if (role === ROLES.ADMIN) {
      return [
        { label: 'Dashboard', route: '/dashboard', icon: 'bi-graph-up' },
        { label: 'Customers', route: '/customers', icon: 'bi-people' },
        { label: 'Products', route: '/products', icon: 'bi-box' },
        { label: 'Orders', route: '/orders', icon: 'bi-receipt' },
        { label: 'Order Items', route: '/order-items', icon: 'bi-list-check' },
        { label: 'Profile', route: '/profile', icon: 'bi-person' }
      ];
    }

    if (role === ROLES.MANAGER) {
      return [
        { label: 'Dashboard', route: '/dashboard', icon: 'bi-graph-up' },
        { label: 'Products', route: '/products', icon: 'bi-box' },
        { label: 'Orders', route: '/orders', icon: 'bi-receipt' },
        { label: 'Order Items', route: '/order-items', icon: 'bi-list-check' },
        { label: 'Profile', route: '/profile', icon: 'bi-person' }
      ];
    }

    return [
      { label: 'Products', route: '/products', icon: 'bi-shop' },
      { label: 'My Cart', route: '/cart', icon: 'bi-bag' },
      { label: 'Favorites', route: '/favorites', icon: 'bi-heart' },
      { label: 'My Orders', route: '/orders', icon: 'bi-receipt' },
      { label: 'Profile', route: '/profile', icon: 'bi-person' }
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

  onItemClick(): void {
    this.itemClick.emit();
  }
}

