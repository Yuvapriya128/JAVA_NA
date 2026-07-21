import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CreateScanRequestDto,
  ScanDto,
  ScanOrderBy,
} from './scan.dto';

@Injectable({
  providedIn: 'root',
})
export class ScanApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = 'http://localhost:9090';

  getScans(): Observable<ScanDto[]> {
    return this.http.get<ScanDto[]>(`${this.baseUrl}/scan`);
  }

  getScanById(id: number): Observable<ScanDto> {
    return this.http.get<ScanDto>(`${this.baseUrl}/scan/${id}`);
  }

  createScan(payload: CreateScanRequestDto): Observable<ScanDto> {
    return this.http.post<ScanDto>(`${this.baseUrl}/scan`, payload);
  }

  deleteScan(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/scan/${id}`);
  }

  searchScans(domainName: string, orderBy: ScanOrderBy): Observable<ScanDto[]> {
    const params = new HttpParams().set('orderBy', orderBy);
    return this.http.get<ScanDto[]>(
      `${this.baseUrl}/scan/search/${encodeURIComponent(domainName)}`,
      { params }
    );
  }

  getHealth(): Observable<unknown> {
    return this.http.get(`${this.baseUrl}/health`, { observe: 'response' });
  }

  getReady(): Observable<unknown> {
    return this.http.get(`${this.baseUrl}/ready`, { observe: 'response' });
  }
}
