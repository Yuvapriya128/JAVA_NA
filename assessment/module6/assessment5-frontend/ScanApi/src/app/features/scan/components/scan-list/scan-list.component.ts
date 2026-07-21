import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ScanApiService } from '../../services/scan-api.service';
import { ScanDto } from '../../services/scan.dto';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { AlertMessageComponent } from '../../../../shared/components/alert-message/alert-message.component';

@Component({
  selector: 'app-scan-list',
  imports: [RouterLink, LoadingComponent, AlertMessageComponent],
  templateUrl: './scan-list.component.html',
  styleUrl: './scan-list.component.css',
})
export class ScanListComponent implements OnInit {
  private readonly scanApiService = inject(ScanApiService);

  readonly scans = signal<ScanDto[]>([]);
  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  ngOnInit(): void {
    this.loadScans();
  }

  loadScans(): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.scanApiService.getScans().subscribe({
      next: (scans) => {
        this.scans.set(scans);
        this.loading.set(false);
      },
      error: (error: Error) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }

  deleteScan(scanId: number): void {
    const confirmed = window.confirm('Are you sure you want to delete this scan?');
    if (!confirmed) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.scanApiService.deleteScan(scanId).subscribe({
      next: () => {
        this.successMessage.set(`Scan ${scanId} deleted successfully.`);
        this.loadScans();
      },
      error: (error: Error) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }
}
