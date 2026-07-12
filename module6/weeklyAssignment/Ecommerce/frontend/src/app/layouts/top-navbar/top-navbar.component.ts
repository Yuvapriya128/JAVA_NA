import { Component, Output, EventEmitter, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/auth/auth.service';
import { AuthStateService } from '../../core/auth/auth-state.service';

@Component({
  selector: 'app-top-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './top-navbar.component.html',
  styleUrl: './top-navbar.component.css'
})
export class TopNavbarComponent {
  @Output() toggleSidebar = new EventEmitter<void>();

  private readonly authService = inject(AuthService);
  private readonly authState = inject(AuthStateService);

  get userLabel(): string {
    return this.authState.currentUser()?.name || 'User';
  }

  get roleLabel(): string {
    return this.authState.currentUser()?.role || '';
  }

  openUserMenu(): void {
    // User menu logic
  }

  onLogout(): void {
    this.authService.logout();
  }

  onToggleSidebar(): void {
    this.toggleSidebar.emit();
  }
}

