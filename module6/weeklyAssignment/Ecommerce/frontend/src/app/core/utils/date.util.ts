/**
 * Utility functions for date operations
 */
export class DateUtil {
  /**
   * Format date as YYYY-MM-DD
   */
  static formatDate(date: Date | string): string {
    const d = typeof date === 'string' ? new Date(date) : date;
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${d.getFullYear()}-${month}-${day}`;
  }

  /**
   * Get date difference in days
   */
  static getDaysDifference(date1: Date, date2: Date): number {
    const diffTime = Math.abs(date2.getTime() - date1.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  /**
   * Check if date is today
   */
  static isToday(date: Date): boolean {
    const today = new Date();
    return date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear();
  }

  /**
   * Get start of day
   */
  static getStartOfDay(date: Date = new Date()): Date {
    date.setHours(0, 0, 0, 0);
    return date;
  }

  /**
   * Get end of day
   */
  static getEndOfDay(date: Date = new Date()): Date {
    date.setHours(23, 59, 59, 999);
    return date;
  }
}

