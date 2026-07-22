import { Component, inject } from '@angular/core';
import { LoadingService } from '../../../core/services/loading.service';

@Component({
  selector: 'app-loading',
  templateUrl: './loading.html',
  styleUrl: './loading.css',
})
export class Loading {
  protected readonly loading = inject(LoadingService);
}
