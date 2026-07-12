import { Directive, Input, TemplateRef, ViewContainerRef, effect } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

/**
 * Directive: appHasRole
 * Shows/hides element based on user role
 * Usage: *appHasRole="'ADMIN'" or *appHasRole="['ADMIN', 'MANAGER']"
 */
@Directive({
  selector: '[appHasRole]',
  standalone: true
})
export class AppHasRoleDirective {
  private requiredRoles: string[] = [];

  @Input()
  set appHasRole(roles: string | string[]) {
    this.requiredRoles = Array.isArray(roles) ? roles : [roles];
    this.updateView();
  }

  constructor(
    private templateRef: TemplateRef<any>,
    private viewContainer: ViewContainerRef,
    private authService: AuthService
  ) {
    effect(() => {
      this.authService.currentUser();
      this.updateView();
    });
  }

  private updateView(): void {
    this.viewContainer.clear();

    const hasRole = this.requiredRoles.length > 0
      ? this.requiredRoles.some(role => this.authService.hasRole(role))
      : false;

    if (hasRole) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
