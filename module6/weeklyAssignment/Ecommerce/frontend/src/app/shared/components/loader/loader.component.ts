import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { SpinnerComponent } from '../spinner/spinner.component';

@Component({
  selector: 'app-loader',
  standalone: true,
  imports: [CommonModule, SpinnerComponent],
  template: `
    <div *ngIf="show" class="position-fixed top-0 start-0 w-100 h-100 d-flex align-items-center justify-content-center bg-body bg-opacity-75 loader-overlay">
      <div class="text-center">
        <app-spinner [showLabel]="false" [variant]="variant"></app-spinner>
        <p class="mt-3 mb-0 text-muted">{{ text }}</p>
      </div>
    </div>
  `,
  styles: [
    `
      .loader-overlay {
        z-index: 1080;
      }
    `
  ]
})
export class LoaderComponent {
  @Input() show = false;
  @Input() text = 'Please wait...';
  @Input() variant: 'primary' | 'secondary' | 'light' | 'dark' = 'primary';
}

