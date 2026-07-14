import { Directive, Input, TemplateRef, ViewContainerRef, effect } from '@angular/core';
import { AuthService } from '../../core/auth/auth.service';

/**
 * Directive: appHasPermission
 * Shows/hides element based on user permission
 * Usage: *appHasPermission="'customer:read'" or *appHasPermission="['customer:read', 'customer:write']"
 */
@Directive({
  selector: '[appHasPermission]',
  standalone: true
})
export class AppHasPermissionDirective {
  private requiredPermissions: string[] = [];

  @Input()
  set appHasPermission(permissions: string | string[]) {
    this.requiredPermissions = Array.isArray(permissions) ? permissions : [permissions];
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

    const hasPermission = this.requiredPermissions.length > 0
      ? this.requiredPermissions.every(permission => this.authService.hasPermission(permission))
      : false;

    if (hasPermission) {
      this.viewContainer.createEmbeddedView(this.templateRef);
    }
  }
}
