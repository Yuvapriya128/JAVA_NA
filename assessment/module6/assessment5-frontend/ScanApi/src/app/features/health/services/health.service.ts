import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, map, tap } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class HealthService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:9090';

  checkHealth(): Observable<void> {
    return this.http
      .get(`${this.baseUrl}/health`, { responseType: 'text' })
      .pipe(
        tap(() => console.info('[HealthService] /health -> OK')),
        map(() => void 0)
      );
  }

  checkReady(): Observable<void> {
    return this.http
      .get(`${this.baseUrl}/ready`, { responseType: 'text' })
      .pipe(
        tap(() => console.info('[HealthService] /ready -> OK')),
        map(() => void 0)
      );
  }
}
