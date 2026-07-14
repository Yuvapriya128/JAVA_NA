import { DOCUMENT } from '@angular/common';
import { ChangeDetectionStrategy, Component, computed, effect, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { AuthStateService } from '../../../core/auth/auth-state.service';
import { AppStateService } from '../../../core/state/app-state.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  private readonly authState = inject(AuthStateService);
  private readonly appState = inject(AppStateService);
  private readonly document = inject(DOCUMENT);

  readonly isMobileSearchOpen = signal(false);

  readonly userLabel = computed(() => this.authState.currentUser()?.name || 'User');
  readonly roleLabel = computed(() => this.authState.currentUser()?.role || '-');
  readonly isDarkMode = computed(() => this.appState.theme() === 'dark');

  constructor() {
    effect(() => {
      this.document.documentElement.setAttribute('data-theme', this.appState.theme());
    });
  }

  openMobileSearch(): void {
    this.isMobileSearchOpen.set(true);
  }

  closeMobileSearch(): void {
    this.isMobileSearchOpen.set(false);
  }

  toggleTheme(): void {
    this.appState.toggleTheme();
  }
}
