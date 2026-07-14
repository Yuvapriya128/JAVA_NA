import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-not-found',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="min-vh-100 d-flex align-items-center justify-content-center bg-light px-3">
      <div class="text-center">
        <h1 class="display-4 fw-bold mb-2">404</h1>
        <h2 class="h4 mb-3">Page not found</h2>
        <p class="text-muted mb-4">The page you requested does not exist or has been moved.</p>
        <a routerLink="/products" class="btn btn-primary me-2">Go to Products</a>
        <a routerLink="/auth/login" class="btn btn-outline-secondary">Back to Login</a>
      </div>
    </section>
  `
})
export class NotFoundComponent {}

