import { Component, computed, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthStateService } from '../../core/auth/auth-state.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'})

export class ProfileComponent {
  private readonly authState = inject(AuthStateService);

  readonly customerId = computed(() => this.authState.currentUser()?.customerId ?? '-');
  readonly name = computed(() => this.authState.currentUser()?.name || '-');
  readonly email = computed(() => this.authState.currentUser()?.email || '-');
  readonly role = computed(() => this.authState.currentUser()?.role || '-');
}

