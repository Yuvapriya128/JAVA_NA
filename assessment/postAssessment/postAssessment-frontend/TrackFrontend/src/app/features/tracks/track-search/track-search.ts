import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Track } from '../../../core/models/track.model';
import { TrackService } from '../../../core/services/track.service';

@Component({
  selector: 'app-track-search',
  imports: [ReactiveFormsModule, DatePipe],
  templateUrl: './track-search.html',
  styleUrl: './track-search.css',
})
export class TrackSearch {
  private readonly fb = inject(FormBuilder);
  private readonly trackService = inject(TrackService);

  protected readonly searching = signal(false);
  protected readonly notFound = signal(false);
  protected readonly result = signal<Track | null>(null);

  protected readonly form = this.fb.nonNullable.group({
    title: ['', [Validators.required]],
  });

  search(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const title = this.form.controls.title.value.trim();
    this.searching.set(true);
    this.notFound.set(false);
    this.result.set(null);

    this.trackService.searchByTitle(title).subscribe({
      next: (track) => {
        if (track) {
          this.result.set(track);
        } else {
          this.notFound.set(true);
        }
        this.searching.set(false);
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404) {
          this.notFound.set(true);
        }
        this.searching.set(false);
      },
    });
  }
}
