import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

export type CheckoutStep = 'cart' | 'address' | 'payment' | 'review' | 'success';

@Component({
  selector: 'app-checkout-stepper',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './checkout-stepper.component.html',
  styleUrl: './checkout-stepper.component.css'
})
export class CheckoutStepperComponent {
  @Input() currentStep: CheckoutStep = 'cart';
  @Output() stepChange = new EventEmitter<CheckoutStep>();

  readonly steps: { id: CheckoutStep; label: string; icon: string }[] = [
    { id: 'cart', label: 'Cart', icon: 'bi-bag-check' },
    { id: 'address', label: 'Address', icon: 'bi-geo-alt' },
    { id: 'payment', label: 'Payment', icon: 'bi-credit-card' },
    { id: 'review', label: 'Review', icon: 'bi-clipboard-check' },
    { id: 'success', label: 'Success', icon: 'bi-check-circle' }
  ];

  isStepComplete(stepId: CheckoutStep): boolean {
    const stepOrder: CheckoutStep[] = ['cart', 'address', 'payment', 'review', 'success'];
    return stepOrder.indexOf(stepId) < stepOrder.indexOf(this.currentStep);
  }

  isStepCurrent(stepId: CheckoutStep): boolean {
    return this.currentStep === stepId;
  }

  isStepPending(stepId: CheckoutStep): boolean {
    const stepOrder: CheckoutStep[] = ['cart', 'address', 'payment', 'review', 'success'];
    return stepOrder.indexOf(stepId) > stepOrder.indexOf(this.currentStep);
  }

  onStepClick(stepId: CheckoutStep): void {
    const stepOrder: CheckoutStep[] = ['cart', 'address', 'payment', 'review', 'success'];
    if (stepOrder.indexOf(stepId) <= stepOrder.indexOf(this.currentStep)) {
      this.stepChange.emit(stepId);
    }
  }
}

