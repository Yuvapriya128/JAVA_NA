import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { CartService } from '../../core/services/cart.service';
import { AuthStateService } from '../../core/auth/auth-state.service';
import { OrderService } from '../../core/services/order.service';
import { OrderItemService } from '../../core/services/order-item.service';
import { OrderRefreshService } from '../../core/services/order-refresh.service';
import { CheckoutService, PaymentMethod } from './checkout.service';
import { OrderRequestDTO } from '../../core/dto/order/order.dto';
import { OrderItemRequestDTO } from '../../core/dto/order-item/orderitem.dto';
import { forkJoin, throwError } from 'rxjs';
import { map, switchMap } from 'rxjs/operators';

interface CartItem {
  product: any;
  quantity: number;
}

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout.component.html',
  styleUrl: './checkout.component.css'
})
export class CheckoutComponent implements OnInit {
  private readonly cartService = inject(CartService);
  private readonly authState = inject(AuthStateService);
  private readonly orderService = inject(OrderService);
  private readonly orderItemService = inject(OrderItemService);
  private readonly orderRefreshService = inject(OrderRefreshService);
  private readonly checkoutService = inject(CheckoutService);
  private readonly router = inject(Router);

  cartItems: CartItem[] = [];
  subtotal = 0;
  delivery = 0;
  tax = 0;
  grandTotal = 0;
  selectedPayment: PaymentMethod | null = null;
  isProcessing = false;
  errorMessage = '';
  processingMessage = '';

  readonly paymentMethods = [
    {
      id: 'CASH' as PaymentMethod,
      title: 'Cash on Delivery',
      description: 'Pay when order arrives.',
      icon: '💵'
    },
    {
      id: 'UPI' as PaymentMethod,
      title: 'UPI (Demo)',
      description: 'Simulated UPI payment.',
      icon: '📱'
    },
    {
      id: 'CARD' as PaymentMethod,
      title: 'Card (Demo)',
      description: 'Simulated card payment.',
      icon: '💳'
    }
  ];

  ngOnInit(): void {
    this.loadCartData();
  }

  loadCartData(): void {
    this.cartItems = this.cartService.cartItems();
    this.calculateTotals();

    if (this.cartItems.length === 0) {
      this.router.navigate(['/cart']);
    }
  }

  calculateTotals(): void {
    this.subtotal = this.cartService.subtotal();
    this.delivery = 0;
    this.tax = 0;
    this.grandTotal = this.subtotal + this.delivery + this.tax;
    this.checkoutService.setOrderTotal(this.grandTotal);
  }

  selectPaymentMethod(method: PaymentMethod): void {
    this.selectedPayment = method;
    this.checkoutService.setPaymentMethod(method);
    this.errorMessage = '';
  }

  async placeOrder(): Promise<void> {
    if (!this.selectedPayment) {
      this.errorMessage = 'Please select a payment method.';
      return;
    }

    this.errorMessage = '';
    const customerId = Number(this.authState.currentUser()?.customerId);

    console.log('=== PLACE ORDER INITIATED ===');
    console.log('Selected Payment:', this.selectedPayment);
    console.log('Customer ID:', customerId);
    console.log('Cart Items Count:', this.cartItems.length);

    if (!Number.isFinite(customerId) || customerId <= 0) {
      this.errorMessage = 'Unable to resolve customer session. Please login again.';
      return;
    }

    if (!this.cartItems.length) {
      this.errorMessage = 'Your cart is empty.';
      return;
    }

    // Set processing state
    this.isProcessing = true;
    this.setProcessingMessage();

    try {
      // Simulate payment
      await this.checkoutService.simulatePayment(this.selectedPayment);
      console.log('Payment simulation completed for:', this.selectedPayment);

      // Prepare order payload
      const totalQuantity = this.cartItems.reduce((sum, item) => sum + Number(item.quantity || 0), 0);
      const leadProductId = Number(this.cartItems[0]?.product?.id);

      if (!Number.isFinite(leadProductId) || leadProductId <= 0 || totalQuantity <= 0) {
        throw new Error('Cart has invalid items.');
      }

      const now = new Date().toISOString();
      const orderPayload: any = {
        customerId,
        productId: leadProductId,
        quantity: totalQuantity,
        orderDate: now,
        totalAmount: this.grandTotal,
        status: 'CONFIRMED',
        paymentMethod: this.selectedPayment,
        paymentStatus: 'SUCCESS'
      };

      console.log('Order Payload prepared:', orderPayload);
      console.log('Order Payload Keys:', Object.keys(orderPayload));

      const orderItemPayloads: OrderItemRequestDTO[] = this.cartItems
        .map((item) => ({
          orderId: 0,
          productId: Number(item.product?.id),
          quantity: Number(item.quantity),
          unitPrice: Number(item.product?.cost),
          totalPrice: Number(item.product?.cost) * Number(item.quantity)
        }))
        .filter((item) => Number.isFinite(item.productId) && item.productId > 0);

      // Create order
      this.orderService.createOrder(orderPayload)
        .pipe(
          switchMap((createdOrder) => {
            console.log('✓ Order created successfully:', createdOrder);
            const orderId = Number(createdOrder?.id);
            if (!Number.isFinite(orderId) || orderId <= 0) {
              return throwError(() => new Error('Order created but could not retrieve ID.'));
            }

            console.log('Creating order items for order ID:', orderId);
            const itemsWithOrderId = orderItemPayloads.map((item) => ({
              ...item,
              orderId
            }));

            return forkJoin(itemsWithOrderId.map((payload) => this.orderItemService.createOrderItem(payload))).pipe(
              map(() => createdOrder)
            );
          })
        )
        .subscribe({
          next: (createdOrder) => {
            console.log('✓ Order and order items created successfully');
            this.cartService.clearCart();
            this.orderRefreshService.triggerRefresh();
            this.router.navigate(['/checkout/success'], { state: { orderId: createdOrder.id, paymentMethod: this.selectedPayment } });
          },
          error: (error) => {
            console.error('✗ Order creation error:', error);
            console.error('Status:', error?.status);
            console.error('Error message:', error?.error?.message);
            console.error('Full error response:', error?.error);
            this.isProcessing = false;
            this.errorMessage = error?.error?.message || 'Failed to place order. Please try again.';
          }
        });
    } catch (error: any) {
      this.isProcessing = false;
      this.errorMessage = error?.message || 'Payment simulation failed.';
    }
  }

  private setProcessingMessage(): void {
    switch (this.selectedPayment) {
      case 'CASH':
        this.processingMessage = 'Processing order...';
        break;
      case 'UPI':
        this.processingMessage = 'Opening UPI...';
        break;
      case 'CARD':
        this.processingMessage = 'Processing Card...';
        break;
      default:
        this.processingMessage = 'Processing...';
    }
  }

  goBack(): void {
    this.router.navigate(['/cart']);
  }
}

