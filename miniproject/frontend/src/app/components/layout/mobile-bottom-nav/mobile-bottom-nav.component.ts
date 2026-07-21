import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { TokenStorageService } from '../../../services/auth/token-storage.service';

@Component({
  selector: 'app-mobile-bottom-nav',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './mobile-bottom-nav.component.html',
  styleUrls: ['./mobile-bottom-nav.component.css']
})
export class MobileBottomNavComponent {
  private readonly tokenStorage = inject(TokenStorageService);

  isUserFlow(): boolean {
    return this.tokenStorage.hasRole(['USER']) && !this.tokenStorage.hasRole(['MANAGER', 'ADMIN']);
  }
}

