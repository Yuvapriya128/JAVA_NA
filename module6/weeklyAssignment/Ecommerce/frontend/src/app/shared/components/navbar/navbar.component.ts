import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthStateService } from '../../../core/auth/auth-state.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class NavbarComponent {
  private readonly authState = inject(AuthStateService);

  readonly userLabel = computed(() => this.authState.currentUser()?.name || 'User');
  readonly roleLabel = computed(() => this.authState.currentUser()?.role || '-');
}
