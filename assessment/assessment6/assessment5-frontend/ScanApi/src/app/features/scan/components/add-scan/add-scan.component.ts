import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ScanApiService } from '../../services/scan-api.service';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { AlertMessageComponent } from '../../../../shared/components/alert-message/alert-message.component';

@Component({
  selector: 'app-add-scan',
  imports: [ReactiveFormsModule, LoadingComponent, AlertMessageComponent],
  templateUrl: './add-scan.component.html',
  styleUrl: './add-scan.component.css',
})
export class AddScanComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly scanApiService = inject(ScanApiService);
  private readonly router = inject(Router);

  readonly loading = signal(false);
  readonly errorMessage = signal('');
  readonly successMessage = signal('');

  // Requires: label(s) + dot + valid TLD (e.g. example.com, my-site.co.in, school.edu)
  private readonly domainPattern =
    /^(?:[a-zA-Z](?:[a-zA-Z0-9-]{0,32}[a-zA-Z0-9])?\.)+[a-zA-Z]{2,}$/;

  readonly form = this.formBuilder.nonNullable.group({
    domainName: [
      '',
      [
        Validators.required,
        Validators.minLength(3),
        Validators.pattern(this.domainPattern),
      ],
    ],
    numPages: [1, [Validators.required, Validators.min(0)]],
    numBrokenLinks: [0, [Validators.required, Validators.min(0)]],
    numMissingImages: [0, [Validators.required, Validators.min(0)]],
  });

  get submitted(): boolean {
    return this.form.touched || this.form.dirty;
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      return;
    }

    this.loading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.scanApiService.createScan(this.form.getRawValue()).subscribe({
      next: () => {
        this.successMessage.set('Scan created successfully. Redirecting to scan list...');
        this.loading.set(false);
        this.router.navigate(['/scans']);
      },
      error: (error: Error) => {
        this.errorMessage.set(error.message);
        this.loading.set(false);
      },
    });
  }
}
