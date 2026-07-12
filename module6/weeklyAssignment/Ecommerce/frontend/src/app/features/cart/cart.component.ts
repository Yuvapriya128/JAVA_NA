import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CartItem, CartService } from '../../core/services/cart.service';
import { ToastComponent } from '../../shared/components/toast/toast.component';
import { ConfirmDialogComponent } from '../../shared/components/dialogs/confirm-dialog.component';

interface CartItemLocal {
  product: any;
  quantity: number;
}

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, ToastComponent, ConfirmDialogComponent],
  templateUrl: './cart.component.html',
  styleUrl: './cart.component.css'
})
export class CartComponent {
  toastShow = false;
  toastTitle = '';
  toastMessage = '';
  toastVariant: 'success' | 'danger' | 'warning' | 'info' = 'info';

  showConfirm = false;
  confirmTitle = 'Confirm';
  confirmMessage = 'Are you sure?';
  confirmText = 'Continue';
  confirmAction: 'clear' | 'remove' | null = null;
  targetItemId: number | null = null;

  actionLoading = false;

  constructor(
    private readonly router: Router,
    private readonly cartService: CartService
  ) {}

  get cartItems(): CartItemLocal[] {
    return this.cartService.cartItems();
  }

  increase(item: CartItemLocal): void {
    this.cartService.increaseQuantity(Number(item.product.id));
  }

  decrease(item: CartItemLocal): void {
    this.cartService.decreaseQuantity(Number(item.product.id));
  }

  remove(item: CartItemLocal): void {
    this.confirmAction = 'remove';
    this.targetItemId = Number(item.product.id);
    this.confirmTitle = 'Remove item';
    this.confirmMessage = 'Do you want to remove this item from your cart?';
    this.confirmText = 'Remove';
    this.showConfirm = true;
  }

  clear(): void {
    this.confirmAction = 'clear';
    this.targetItemId = null;
    this.confirmTitle = 'Clear cart';
    this.confirmMessage = 'This will remove all products from your cart.';
    this.confirmText = 'Clear cart';
    this.showConfirm = true;
  }

  get subtotalAmount(): number {
    return this.cartService.subtotal();
  }

  get deliveryAmount(): number {
    return 0;
  }

  get taxAmount(): number {
    return 0;
  }

  get grandTotal(): number {
    return this.subtotalAmount + this.deliveryAmount + this.taxAmount;
  }

  goToProducts(): void {
    this.router.navigate(['/products']);
  }

  proceedToCheckout(): void {
    if (!this.cartItems.length) {
      return;
    }
    this.router.navigate(['/checkout']);
  }

  onConfirmAction(): void {
    this.actionLoading = true;

    if (this.confirmAction === 'clear') {
      this.cartService.clearCart();
      this.showToast('success', 'Cart cleared', 'All items were removed.');
    }

    if (this.confirmAction === 'remove' && this.targetItemId) {
      this.cartService.removeProduct(this.targetItemId);
      this.showToast('info', 'Item removed', 'Product removed from cart.');
    }

    this.resetConfirm();
    this.actionLoading = false;
  }

  onCancelAction(): void {
    this.resetConfirm();
  }

  private resetConfirm(): void {
    this.showConfirm = false;
    this.confirmAction = null;
    this.targetItemId = null;
  }

  private showToast(variant: 'success' | 'danger' | 'warning' | 'info', title: string, message: string): void {
    this.toastVariant = variant;
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastShow = true;
  }
}
