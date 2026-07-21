import { inject, Injectable } from '@angular/core';
import { AuthService } from './auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService {
  private readonly authService = inject(AuthService);

  /**
   * Check if user has a specific role
   */
  hasRole(role: string): boolean {
    return this.authService.hasRole(role);
  }

  /**
   * Check if user has any of the specified roles
   */
  hasAnyRole(roles: string[]): boolean {
    return this.authService.hasAnyRole(roles);
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.authService.isAdmin();
  }

  /**
   * Check if user is manager
   */
  isManager(): boolean {
    return this.authService.isManager();
  }

  /**
   * Check if user is regular user
   */
  isUser(): boolean {
    return this.authService.isUser();
  }

  /**
   * Check if user is manager or admin (elevated permissions)
   */
  isManagerOrAdmin(): boolean {
    return this.isManager() || this.isAdmin();
  }
}
