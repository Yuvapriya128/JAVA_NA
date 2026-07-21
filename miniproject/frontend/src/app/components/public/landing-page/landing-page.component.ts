import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

interface LandingStat {
  value: string;
  label: string;
}

@Component({
  selector: 'app-landing-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './landing-page.component.html',
  styleUrls: ['./landing-page.component.css']
})
export class LandingPageComponent {
  readonly year = new Date().getFullYear();

  readonly stats: LandingStat[] = [
    { value: '5,000+', label: 'Active Customers' },
    { value: 'INR 500Cr', label: 'Loans Disbursed' },
    { value: '24/7', label: 'Support Available' },
    { value: '99.9%', label: 'Uptime' }
  ];
}
