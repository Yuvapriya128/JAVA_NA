import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/auth/auth.service';
import { AuthStateService } from '../../../core/auth/auth-state.service';
import { ROLES } from '../../../core/auth/constants/roles.constants';
import { type AppNavigationItem } from '../../navigation/navigation.config';
import { NavigationService } from '../../navigation/navigation.service';

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
  private readonly navigationService = inject(NavigationService);
  readonly roles = ROLES;

  readonly role = computed(() => this.authState.effectiveRole());
  readonly isViewingAsUser = computed(
    () => this.authState.realRole() === ROLES.ADMIN && this.authState.effectiveRole() === ROLES.USER
  );
  readonly userName = computed(() => this.authState.currentUser()?.name || 'User');

  readonly mainMenuItems = computed(() =>
    this.navigationService.desktopItems().filter((item) => item.section === 'main')
  );

  readonly accountMenuItems = computed(() =>
    this.navigationService.desktopItems().filter((item) => item.section === 'account')
  );

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

  getBadgeCount(item: AppNavigationItem): number {
    return this.navigationService.getBadgeCount(item);
  }
}
