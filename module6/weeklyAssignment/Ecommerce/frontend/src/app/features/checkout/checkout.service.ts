import { Injectable } from '@angular/core';
import { signal } from '@angular/core';

export type PaymentMethod = 'CASH' | 'UPI' | 'CARD';

export interface CheckoutState {
  paymentMethod: PaymentMethod | null;
  isProcessing: boolean;
  orderTotal: number;
}

@Injectable({
  providedIn: 'root'
})
export class CheckoutService {
  private readonly checkoutStateSignal = signal<CheckoutState>({
    paymentMethod: null,
    isProcessing: false,
    orderTotal: 0
  });

  readonly checkoutState = this.checkoutStateSignal.asReadonly();

  setPaymentMethod(method: PaymentMethod): void {
    const current = this.checkoutStateSignal();
    this.checkoutStateSignal.set({ ...current, paymentMethod: method });
  }

  setProcessing(isProcessing: boolean): void {
    const current = this.checkoutStateSignal();
    this.checkoutStateSignal.set({ ...current, isProcessing });
  }

  setOrderTotal(total: number): void {
    const current = this.checkoutStateSignal();
    this.checkoutStateSignal.set({ ...current, orderTotal: total });
  }

  reset(): void {
    this.checkoutStateSignal.set({
      paymentMethod: null,
      isProcessing: false,
      orderTotal: 0
    });
  }

  async simulatePayment(method: PaymentMethod): Promise<void> {
    this.setProcessing(true);
    const delayMs = method === 'CASH' ? 1000 : 2000;
    await new Promise((resolve) => setTimeout(resolve, delayMs));
    this.setProcessing(false);
  }
}

