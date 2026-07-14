import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-access-denied',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="min-vh-100 d-flex align-items-center justify-content-center bg-light px-3">
      <div class="text-center">
        <h1 class="display-5 fw-bold mb-2">403</h1>
        <h2 class="h4 mb-3">Access denied</h2>
        <p class="text-muted mb-4">You do not have permission to view this page.</p>
        <a routerLink="/dashboard" class="btn btn-primary me-2">Go to Dashboard</a>
        <a routerLink="/auth/login" class="btn btn-outline-secondary">Switch Account</a>
      </div>
    </section>
  `
})
export class AccessDeniedComponent {}

