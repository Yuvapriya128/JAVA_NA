import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';

@Component({
  selector: 'app-modal',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="show" class="modal d-block" tabindex="-1" role="dialog" (click)="onBackdrop($event)">
      <div class="modal-dialog" [ngClass]="sizeClass" role="document">
        <div class="modal-content">
          <div class="modal-header">
            <h5 class="modal-title">{{ title }}</h5>
            <button type="button" class="btn-close" aria-label="Close" (click)="close.emit()"></button>
          </div>
          <div class="modal-body">
            <ng-content></ng-content>
          </div>
          <div *ngIf="showFooter" class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" (click)="close.emit()">{{ cancelText }}</button>
            <button type="button" class="btn btn-primary" (click)="confirm.emit()">{{ confirmText }}</button>
          </div>
        </div>
      </div>
    </div>
    <div *ngIf="show" class="modal-backdrop fade show"></div>
  `
})
export class ModalComponent {
  @Input() show = false;
  @Input() title = 'Modal';
  @Input() size: 'sm' | 'md' | 'lg' | 'xl' = 'md';
  @Input() showFooter = true;
  @Input() cancelText = 'Cancel';
  @Input() confirmText = 'Confirm';
  @Input() closeOnBackdrop = true;

  @Output() close = new EventEmitter<void>();
  @Output() confirm = new EventEmitter<void>();

  get sizeClass(): string {
    if (this.size === 'sm') return 'modal-sm';
    if (this.size === 'lg') return 'modal-lg';
    if (this.size === 'xl') return 'modal-xl';
    return '';
  }

  onBackdrop(event: MouseEvent): void {
    if (!this.closeOnBackdrop) return;
    if ((event.target as HTMLElement).classList.contains('modal')) {
      this.close.emit();
    }
  }
}

