import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'dateFormat',
  standalone: true
})
export class DateFormatPipe implements PipeTransform {
  transform(value: string | Date, format: string = 'short'): string {
    if (!value) return '';

    const date = typeof value === 'string' ? new Date(value) : value;

    switch (format) {
      case 'short':
        return date.toLocaleDateString('en-US');
      case 'long':
        return date.toLocaleDateString('en-US', {
          weekday: 'long',
          year: 'numeric',
          month: 'long',
          day: 'numeric'
        });
      case 'time':
        return date.toLocaleTimeString('en-US');
      default:
        return date.toISOString();
    }
  }
}

