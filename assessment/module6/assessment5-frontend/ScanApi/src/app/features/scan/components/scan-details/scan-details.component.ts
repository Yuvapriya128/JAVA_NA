import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ScanApiService } from '../../services/scan-api.service';
import { ScanDto } from '../../services/scan.dto';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { AlertMessageComponent } from '../../../../shared/components/alert-message/alert-message.component';

@Component({
  selector: 'app-scan-details',
  imports: [RouterLink, LoadingComponent, AlertMessageComponent],
  templateUrl: './scan-details.component.html',
  styleUrl: './scan-details.component.css',
})
export class ScanDetailsComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly scanApiService = inject(ScanApiService);

  readonly scan = signal<ScanDto | null>(null);
  readonly loading = signal(false);
  readonly errorMessage = signal('');

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    if (Number.isNaN(id)) {
      this.errorMessage.set('Invalid scan id.');
      return;
    }
    this.fetchScan(id);
  }

  private fetchScan(id: number): void {
    this.loading.set(true);
    this.errorMessage.set('');

    this.scanApiService.getScanById(id).subscribe({
      next: (scan) => {
        this.scan.set(scan);
        this.loading.set(false);
      },
      error: (error: Error) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }
}
