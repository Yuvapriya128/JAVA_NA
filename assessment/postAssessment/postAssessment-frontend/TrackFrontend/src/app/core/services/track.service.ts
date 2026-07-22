import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Track, TrackRequest } from '../models/track.model';

/**
 * Typed gateway to the music tracks backend API.
 * All endpoint paths and payload field names match the backend contract exactly.
 */
@Injectable({ providedIn: 'root' })
export class TrackService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/tracks`;

  /** GET /tracks — list all tracks. */
  getAll(): Observable<Track[]> {
    return this.http.get<Track[]>(this.baseUrl);
  }

  /** GET /tracks/search?title=... — find a single track by title. */
  searchByTitle(title: string): Observable<Track> {
    const params = { title };
    return this.http.get<Track>(`${this.baseUrl}/search`, { params });
  }

  /** POST /tracks — create a new track. */
  create(payload: TrackRequest): Observable<Track> {
    return this.http.post<Track>(this.baseUrl, payload);
  }

  /** DELETE /tracks/{trackId} — remove a track. */
  delete(trackId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${trackId}`);
  }
}
