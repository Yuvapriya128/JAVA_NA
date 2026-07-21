import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ScanApiService } from '../../services/scan-api.service';
import { ScanDto, ScanOrderBy } from '../../services/scan.dto';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { AlertMessageComponent } from '../../../../shared/components/alert-message/alert-message.component';

@Component({
  selector: 'app-scan-search',
  imports: [FormsModule, RouterLink, LoadingComponent, AlertMessageComponent],
  templateUrl: './scan-search.component.html',
  styleUrl: './scan-search.component.css',
})
export class ScanSearchComponent {
  private readonly scanApiService = inject(ScanApiService);

  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');
  readonly scans = signal<ScanDto[]>([]);

  domainName = '';
  orderBy: ScanOrderBy = 'id';
  private hasSearched = false;

  readonly orderByOptions: ScanOrderBy[] = [
    'id',
    'domainName',
    'numPages',
    'numBrokenLinks',
    'numMissingImages',
    'deleted',
  ];

  onOrderByChange(): void {
    // Re-run the search automatically once the user has already searched once.
    if (this.hasSearched && this.domainName.trim()) {
      this.onSearch();
    }
  }

  onSearch(): void {
    this.errorMessage.set('');
    this.successMessage.set('');

    if (!this.domainName.trim()) {
      this.errorMessage.set('Please enter a domain name to search.');
      return;
    }

    this.loading.set(true);

    this.scanApiService
      .searchScans(this.domainName.trim(), this.orderBy)
      .subscribe({
        next: (scans) => {
          this.hasSearched = true;
          this.scans.set(scans);
          this.successMessage.set(`Found ${scans.length} result(s).`);
          this.loading.set(false);
        },
        error: (error: Error) => {
          this.errorMessage.set(error.message);
          this.loading.set(false);
        },
      });
  }
}
