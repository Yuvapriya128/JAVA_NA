/**
 * Utility functions for currency operations
 */
export class CurrencyUtil {
  /**
   * Format number as currency
   */
  static formatCurrency(value: number, currency: string = 'USD'): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency
    }).format(value);
  }

  /**
   * Parse currency string to number
   */
  static parseCurrency(value: string): number {
    return parseFloat(value.replace(/[^\d.-]/g, ''));
  }

  /**
   * Calculate percentage
   */
  static calculatePercentage(value: number, percentage: number): number {
    return value * (percentage / 100);
  }

  /**
   * Calculate discount
   */
  static calculateDiscount(originalPrice: number, discountPercent: number): number {
    return originalPrice - this.calculatePercentage(originalPrice, discountPercent);
  }

  /**
   * Calculate tax
   */
  static calculateTax(amount: number, taxPercent: number): number {
    return this.calculatePercentage(amount, taxPercent);
  }

  /**
   * Calculate total with tax
   */
  static calculateTotalWithTax(amount: number, taxPercent: number): number {
    return amount + this.calculateTax(amount, taxPercent);
  }
}

