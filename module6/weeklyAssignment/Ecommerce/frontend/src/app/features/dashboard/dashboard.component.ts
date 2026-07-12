import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="p-4">
      <div class="alert alert-info">
        <i class="bi bi-info-circle"></i>
        Dashboard coming soon
      </div>
    </div>
  `
})
export class DashboardComponent {}

