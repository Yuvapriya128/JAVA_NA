import { Component, OnInit, inject, signal } from '@angular/core';
import { ScanApiService } from '../../scan/services/scan-api.service';

@Component({
  selector: 'app-health-status',
  templateUrl: './health-status.component.html',
})
export class HealthStatusComponent implements OnInit {
  private readonly scanApiService = inject(ScanApiService);

  readonly healthOk = signal<boolean | null>(null);
  readonly readyOk = signal<boolean | null>(null);

  ngOnInit(): void {
    this.checkHealth();
    this.checkReady();
  }

  private checkHealth(): void {
    this.scanApiService.getHealth().subscribe({
      next: () => this.healthOk.set(true),
      error: () => this.healthOk.set(false),
    });
  }

  private checkReady(): void {
    this.scanApiService.getReady().subscribe({
      next: () => this.readyOk.set(true),
      error: () => this.readyOk.set(false),
    });
  }
}
