import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-avatar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="position-relative d-inline-block">
      <img
        *ngIf="imageUrl"
        [src]="imageUrl"
        [alt]="altText"
        class="rounded-circle object-fit-cover"
        [style.width.px]="sizePx"
        [style.height.px]="sizePx" />
      <div
        *ngIf="!imageUrl"
        class="rounded-circle d-flex align-items-center justify-content-center bg-secondary-subtle text-secondary-emphasis fw-semibold"
        [style.width.px]="sizePx"
        [style.height.px]="sizePx">
        {{ initials }}
      </div>
      <span *ngIf="showStatus" class="position-absolute bottom-0 end-0 p-1 border border-2 border-white rounded-circle" [ngClass]="statusClass"></span>
    </div>
  `
})
export class AvatarComponent {
  @Input() imageUrl = '';
  @Input() name = '';
  @Input() sizePx = 40;
  @Input() altText = 'Avatar';
  @Input() showStatus = false;
  @Input() status: 'online' | 'offline' | 'busy' = 'offline';

  get initials(): string {
    return this.name
      .split(' ')
      .filter(Boolean)
      .slice(0, 2)
      .map(word => word[0]?.toUpperCase())
      .join('') || 'U';
  }

  get statusClass(): string {
    if (this.status === 'online') return 'bg-success';
    if (this.status === 'busy') return 'bg-danger';
    return 'bg-secondary';
  }
}

