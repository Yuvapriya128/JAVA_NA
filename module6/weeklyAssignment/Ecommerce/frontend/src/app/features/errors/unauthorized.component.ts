import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-unauthorized',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <section class="min-vh-100 d-flex align-items-center justify-content-center bg-light px-3">
      <div class="text-center">
        <h1 class="display-5 fw-bold mb-2">401</h1>
        <h2 class="h4 mb-3">Unauthorized</h2>
        <p class="text-muted mb-4">Please sign in to continue.</p>
        <a routerLink="/auth/login" class="btn btn-primary">Sign In</a>
      </div>
    </section>
  `
})
export class UnauthorizedComponent {}

