import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../../core/auth/auth.service';

@Component({
  selector: 'app-logout',
  standalone: true,
  templateUrl: './logout.component.html',
  styleUrl: './logout.component.css'})
export class LogoutComponent implements OnInit {
  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    // Keep reset for auth UI state, then run canonical logout flow.
    this.authService.resetAuthStatus();
    this.authService.logout();
  }
}

