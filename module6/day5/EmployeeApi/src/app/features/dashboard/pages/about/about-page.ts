import { Component } from '@angular/core';
import { PageHeader } from '../../../../shared/page-header/page-header';

@Component({
  selector: 'app-about-page',
  standalone: true,
  imports: [PageHeader],
  template: `
    <app-page-header title="About" subtitle="Employee dashboard powered by Angular and Spring Boot." icon="bi-info-circle"></app-page-header>
    <div class="card shadow-sm">
      <div class="card-body">
        <p class="mb-0 d-flex align-items-center gap-2">
          <i class="bi bi-check2-circle text-success" aria-hidden="true"></i>
          <span>This application manages employee records with full CRUD operations.</span>
        </p>
      </div>
    </div>
  `
})
export class AboutPage {}

