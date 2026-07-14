import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-checkout-success',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout-success.component.html',
  styleUrl: './checkout-success.component.css'
})
export class CheckoutSuccessComponent implements OnInit {
  private readonly router = inject(Router);

  orderId: number | null = null;
  paymentMethod: string | null = null;

  ngOnInit(): void {
    const navigation = this.router.getCurrentNavigation();
    const state = navigation?.extras?.state || window.history.state;

    if (state?.orderId) {
      this.orderId = state.orderId;
      this.paymentMethod = state.paymentMethod || null;
    } else {
      this.router.navigate(['/cart']);
    }
  }

  continueShopping(): void {
    this.router.navigate(['/products']);
  }

  goToOrders(): void {
    this.router.navigate(['/orders']);
  }

  getPaymentMethodLabel(method: string | null): string {
    if (!method) return 'N/A';
    const labels: { [key: string]: string } = {
      CASH: 'Cash on Delivery',
      UPI: 'UPI',
      CARD: 'Card'
    };
    return labels[method] || method;
  }
}

