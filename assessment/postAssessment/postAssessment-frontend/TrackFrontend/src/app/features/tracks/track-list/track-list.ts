import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { Track } from '../../../core/models/track.model';
import { RequestStatus, createStatus } from '../../../core/models/request-status.model';
import { NotificationService } from '../../../core/services/notification.service';
import { TrackService } from '../../../core/services/track.service';

@Component({
  selector: 'app-track-list',
  imports: [DatePipe, RouterLink],
  templateUrl: './track-list.html',
  styleUrl: './track-list.css',
})
export class TrackList implements OnInit {
  private readonly trackService = inject(TrackService);
  private readonly notifications = inject(NotificationService);

  protected readonly tracks = signal<Track[]>([]);
  protected readonly deletingId = signal<number | null>(null);

  /** loading (image shown), error (message) and success flags for the list fetch. */
  protected readonly status = createStatus();

  ngOnInit(): void {
    this.loadTracks();
  }

  loadTracks(): void {
    this.status.set({ loading: true, error: null, success: false });
    this.trackService.getAll().subscribe({
      next: (tracks) => {
        this.tracks.set(tracks);
        this.status.set({ loading: false, error: null, success: true });
      },
      error: () =>
        this.status.set({
          loading: false,
          error: 'Unable to load tracks. Please try again.',
          success: false,
        } satisfies RequestStatus),
    });
  }

  confirmDelete(track: Track): void {
    const confirmed = confirm(`Delete "${track.title}"? This cannot be undone.`);
    if (!confirmed) {
      return;
    }
    this.delete(track);
  }

  private delete(track: Track): void {
    this.deletingId.set(track.id);
    this.trackService.delete(track.id).subscribe({
      next: () => {
        this.tracks.update((list) => list.filter((t) => t.id !== track.id));
        this.notifications.success(`Deleted "${track.title}".`);
        this.deletingId.set(null);
      },
      error: () => this.deletingId.set(null),
    });
  }
}
