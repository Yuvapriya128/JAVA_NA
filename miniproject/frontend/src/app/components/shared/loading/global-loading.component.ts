import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingStateService } from '../../../services/shared/loading-state.service';

@Component({
  selector: 'app-global-loading',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './global-loading.component.html',
  styleUrls: ['./global-loading.component.css']
})
export class GlobalLoadingComponent {
  readonly loadingState = inject(LoadingStateService);
}
