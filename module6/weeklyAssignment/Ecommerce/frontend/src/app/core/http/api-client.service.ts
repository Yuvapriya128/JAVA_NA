import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class ApiClientService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  /**
   * GET request
   */
  get<T>(endpoint: string, options?: { params?: HttpParams }): Observable<T> {
    return this.http.get<T>(`${this.apiUrl}${endpoint}`, options);
  }

  /**
   * POST request
   */
  post<T>(endpoint: string, data: any): Observable<T> {
    const fullUrl = `${this.apiUrl}${endpoint}`;
    console.log('[ApiClientService] POST Request');
    console.log('[ApiClientService] Full URL:', fullUrl);
    console.log('[ApiClientService] Endpoint:', endpoint);
    console.log('[ApiClientService] Payload:', data);
    return this.http.post<T>(fullUrl, data);
  }

  /**
   * PUT request
   */
  put<T>(endpoint: string, data: any): Observable<T> {
    return this.http.put<T>(`${this.apiUrl}${endpoint}`, data);
  }

  /**
   * PATCH request
   */
  patch<T>(endpoint: string, data: any): Observable<T> {
    return this.http.patch<T>(`${this.apiUrl}${endpoint}`, data);
  }

  /**
   * DELETE request
   */
  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<T>(`${this.apiUrl}${endpoint}`);
  }
}

