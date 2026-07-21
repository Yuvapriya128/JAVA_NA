import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-stat-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stat-card.component.html',
  styleUrls: ['./stat-card.component.css']})
export class StatCardComponent {
  @Input() title = '';
  @Input() value = '';
  @Input() trend = '';
  @Input() icon = 'bi-graph-up';
  @Input() tone: 'primary' | 'success' | 'warning' | 'danger' | 'info' = 'primary';
}

