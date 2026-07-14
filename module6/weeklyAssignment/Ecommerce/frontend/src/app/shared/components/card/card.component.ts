import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <section class="card h-100" [ngClass]="cardClasses">
      <header *ngIf="title || subtitle || showHeader" class="card-header bg-white border-bottom">
        <h6 *ngIf="title" class="mb-1">{{ title }}</h6>
        <small *ngIf="subtitle" class="text-muted">{{ subtitle }}</small>
      </header>
      <div class="card-body" [ngClass]="bodyClass">
        <ng-content></ng-content>
      </div>
      <footer *ngIf="showFooter" class="card-footer bg-white">
        <ng-content select="[card-footer]"></ng-content>
      </footer>
    </section>
  `
})
export class CardComponent {
  @Input() title = '';
  @Input() subtitle = '';
  @Input() showHeader = false;
  @Input() showFooter = false;
  @Input() bodyClass = '';
  @Input() elevated = false;

  get cardClasses(): string[] {
    return this.elevated ? ['shadow-sm'] : [];
  }
}

