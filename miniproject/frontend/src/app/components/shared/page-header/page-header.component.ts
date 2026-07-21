import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-page-header',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './page-header.component.html',
  styleUrls: ['./page-header.component.css']})
export class PageHeaderComponent {
  @Input() title = '';
  @Input() subtitle = 'Loan Management';
  @Input() buttonLabel = '';
  @Input() buttonRoute = '';
}

