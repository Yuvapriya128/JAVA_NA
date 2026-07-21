import { CommonModule } from '@angular/common';
import { Component, HostListener, inject } from '@angular/core';
import { ConfirmationDialogService } from '../../../services/shared/confirmation-dialog.service';

@Component({
  selector: 'app-confirmation-modal',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirmation-modal.component.html',
  styleUrls: ['./confirmation-modal.component.css']})
export class ConfirmationModalComponent {
  readonly confirmationDialog = inject(ConfirmationDialogService);

  onBackdropClick(event: MouseEvent): void {
    if ((event.target as HTMLElement).classList.contains('confirmation-backdrop')) {
      this.confirmationDialog.cancel();
    }
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    if (this.confirmationDialog.state().visible) {
      this.confirmationDialog.cancel();
    }
  }
}
