import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

export interface StatCard {
  title: string;
  value: string | number;
  unit?: string;
  icon: string;
  trend?: number; // percentage change
  color: 'primary' | 'success' | 'warning' | 'info' | 'danger';
}

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stat-card.component.html',
  styleUrl: './stat-card.component.css'
})
export class StatCardComponent {
  @Input() stat!: StatCard;

  Math = Math;

  getColorClass(): string {
    const colors: { [key: string]: string } = {
      primary: 'stat-primary',
      success: 'stat-success',
      warning: 'stat-warning',
      info: 'stat-info',
      danger: 'stat-danger'
    };
    return colors[this.stat.color] || 'stat-primary';
  }

  isTrendPositive(): boolean {
    return (this.stat.trend || 0) >= 0;
  }
}

