import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-notification-card',
  standalone: true,
  templateUrl: './notification-card.component.html',
  styleUrls: ['./notification-card.component.css'],
})
export class NotificationCardComponent {
  @Input() title = "Today's Highlights";
  @Input() items: string[] = [];
}

