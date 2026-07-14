import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-skeleton',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="placeholder-wave" [ngClass]="wrapperClass">
      <span class="placeholder d-block" [ngClass]="shapeClass" [style.height]="height" [style.width]="width"></span>
    </div>
  `
})
export class SkeletonComponent {
  @Input() width = '100%';
  @Input() height = '1rem';
  @Input() rounded = true;
  @Input() wrapperClass = '';

  get shapeClass(): string[] {
    return this.rounded ? ['rounded'] : [];
  }
}

