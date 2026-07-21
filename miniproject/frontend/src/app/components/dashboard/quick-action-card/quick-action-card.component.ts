import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-quick-action-card',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './quick-action-card.component.html',
  styleUrls: ['./quick-action-card.component.css'],
})
export class QuickActionCardComponent {
  @Input() title = 'Quick Actions';
  @Input() actions: Array<{ label: string; icon: string; className: string; routerLink?: string }> = [];
}

