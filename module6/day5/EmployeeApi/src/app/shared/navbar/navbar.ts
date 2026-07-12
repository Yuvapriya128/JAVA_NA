import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary shadow-sm">
      <div class="container">
        <a class="navbar-brand fw-semibold d-flex align-items-center gap-2" routerLink="/">
          <i class="bi bi-people-fill" aria-hidden="true"></i>
          <span>Employee Portal</span>
        </a>

        <div class="d-flex align-items-center gap-2 ms-auto flex-wrap">
          <a
            class="btn btn-sm btn-primary border border-primary-subtle"
            routerLink="/"
            routerLinkActive="active"
            [routerLinkActiveOptions]="{ exact: true }"
            aria-label="Go to dashboard"
          >
            <i class="bi bi-speedometer2 me-1" aria-hidden="true"></i>Dashboard
          </a>
          <a
            class="btn btn-sm btn-primary border border-primary-subtle"
            routerLink="/employees"
            routerLinkActive="active"
            [routerLinkActiveOptions]="{ exact: true }"
            aria-label="View employees"
          >
            <i class="bi bi-person-lines-fill me-1" aria-hidden="true"></i>Employees
          </a>
          <a class="btn btn-sm btn-light" routerLink="/employees/add" routerLinkActive="active" aria-label="Add employee">
            <i class="bi bi-person-plus me-1" aria-hidden="true"></i>Add
          </a>
          <a class="btn btn-sm btn-primary border border-primary-subtle" routerLink="/about" routerLinkActive="active" aria-label="About">
            <i class="bi bi-info-circle me-1" aria-hidden="true"></i>About
          </a>
        </div>
      </div>
    </nav>
  `
})
export class Navbar {}
