import { Component, inject, signal } from '@angular/core';
import {
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { Router } from '@angular/router';
import { TrackRequest } from '../../../core/models/track.model';
import { NotificationService } from '../../../core/services/notification.service';
import { TrackService } from '../../../core/services/track.service';

@Component({
  selector: 'app-track-create',
  imports: [ReactiveFormsModule],
  templateUrl: './track-create.html',
  styleUrl: './track-create.css',
})
export class TrackCreate {
  private readonly fb = inject(FormBuilder);
  private readonly trackService = inject(TrackService);
  private readonly notifications = inject(NotificationService);
  private readonly router = inject(Router);

  protected readonly submitting = signal(false);

  protected readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.minLength(2)]],
    albumName: ['', [Validators.required]],
    releaseDate: ['', [Validators.required]],
    playCount: [0, [Validators.required, Validators.min(0), Validators.pattern(/^\d+$/)]],
  });

  protected controlInvalid(name: keyof typeof this.form.controls): boolean {
    const control = this.form.controls[name];
    return control.invalid && (control.touched || control.dirty);
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitting.set(true);
    const payload = this.form.getRawValue() as TrackRequest;

    this.trackService.create(payload).subscribe({
      next: (created) => {
        this.notifications.success(`Track "${created.title}" created successfully.`);
        this.submitting.set(false);
        this.router.navigate(['/tracks']);
      },
      error: () => this.submitting.set(false),
    });
  }

  reset(): void {
    this.form.reset({ title: '', albumName: '', releaseDate: '', playCount: 0 });
  }
}
