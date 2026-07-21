import { Component, Input } from '@angular/core';
import { CustomerResponse } from '../../../services/customer/customer.service';

@Component({
  selector: 'app-profile-card',
  standalone: true,
  templateUrl: './profile-card.component.html',
  styleUrls: ['./profile-card.component.css']})
export class ProfileCardComponent {
  @Input() profile: CustomerResponse | null = null;

  initials(): string {
    const name = this.profile?.customerName?.trim() || '';
    if (!name) {
      return 'NA';
    }
    const parts = name.split(/\s+/).filter(Boolean);
    if (parts.length === 1) {
      return parts[0].slice(0, 2).toUpperCase();
    }
    return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
  }

  roleLabel(): string {
    const role = (this.profile?.role || 'USER').toUpperCase();
    if (role === 'ADMIN') {
      return 'Administrator';
    }
    if (role === 'MANAGER') {
      return 'Manager';
    }
    return 'User';
  }
}

