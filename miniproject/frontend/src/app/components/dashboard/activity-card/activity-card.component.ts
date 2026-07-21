import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-activity-card',
  standalone: true,
  templateUrl: './activity-card.component.html',
  styleUrls: ['./activity-card.component.css']})
export class ActivityCardComponent {
  @Input() title = 'Recent Activities';
  @Input() activities: string[] = [];
}

