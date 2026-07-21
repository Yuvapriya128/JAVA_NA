import { Component, Input } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './empty-state.component.html',
  styleUrls: ['./empty-state.component.css'],
})
export class EmptyStateComponent {
  @Input() title = 'No Data Available';
  @Input() subtitle = 'There are currently no records to display.';
  @Input() iconClass = 'bi bi-inbox';
  @Input() actionLabel = 'Refresh';
  @Input() actionRoute = '';
}
