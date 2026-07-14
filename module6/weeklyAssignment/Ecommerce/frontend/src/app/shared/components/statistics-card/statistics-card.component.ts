import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-statistics-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './statistics-card.component.html',
  styleUrl: './statistics-card.component.css'})
export class StatisticsCardComponent {
  @Input() icon = '📊';
  @Input() title = 'Metric';
  @Input() value = '0';
  @Input() changePercent = 0;
}

