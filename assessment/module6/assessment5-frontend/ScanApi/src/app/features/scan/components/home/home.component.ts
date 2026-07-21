import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ScanApiService } from '../../services/scan-api.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { AlertMessageComponent } from '../../../../shared/components/alert-message/alert-message.component';
import { HealthService } from '../../../health/services/health.service';
import { switchMap } from 'rxjs';

type HealthState = 'online' | 'offline' | 'checking';
type ReadyState = 'ready' | 'not-ready' | 'checking';

@Component({
  selector: 'app-home',
  imports: [
    RouterLink,
    LoadingComponent,
    AlertMessageComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent implements OnInit {
  private readonly scanApiService = inject(ScanApiService);
  private readonly healthService = inject(HealthService);

  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly totalScans = signal(0);
  readonly totalPages = signal(0);
  readonly totalBrokenLinks = signal(0);
  readonly totalMissingImages = signal(0);
  readonly totalDeleted = signal(0);
  readonly healthStatus = signal<HealthState>('checking');
  readonly readyStatus = signal<ReadyState>('checking');
  readonly systemStatus = signal({
    loading: false,
    error: '',
    success: false,
  });

  ngOnInit(): void {
    this.loadDashboard();
    this.checkSystemStatus();
  }

  loadDashboard(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.scanApiService.getScans().subscribe({
      next: (scans) => {
        this.totalScans.set(scans.length);
        this.totalPages.set(
          scans.reduce((sum, scan) => sum + (scan.numPages ?? 0), 0)
        );
        this.totalBrokenLinks.set(
          scans.reduce((sum, scan) => sum + (scan.numBrokenLinks ?? 0), 0)
        );
        this.totalMissingImages.set(
          scans.reduce((sum, scan) => sum + (scan.numMissingImages ?? 0), 0)
        );
        this.totalDeleted.set(
          scans.filter((scan) => scan.deleted).length
        );
        this.loading.set(false);
      },
      error: (error: Error) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }

  private checkSystemStatus(): void {
    this.systemStatus.set({ loading: true, error: '', success: false });
    this.healthStatus.set('checking');
    this.readyStatus.set('checking');

    this.healthService
      .checkHealth()
      .pipe(
        switchMap(() => {
          this.healthStatus.set('online');
          return this.healthService.checkReady();
        })
      )
      .subscribe({
        next: () => {
          this.readyStatus.set('ready');
          this.systemStatus.set({ loading: false, error: '', success: true });
        },
        error: (error: Error) => {
          const isHealthFailure = this.healthStatus() !== 'online';

          if (isHealthFailure) {
            this.healthStatus.set('offline');
            this.readyStatus.set('not-ready');
            this.systemStatus.set({
              loading: false,
              error: `The backend service is currently unavailable. ${error.message}`,
              success: false,
            });
            return;
          }

          this.readyStatus.set('not-ready');
          this.systemStatus.set({
            loading: false,
            error: error.message,
            success: false,
          });
        },
      });
  }
}
