import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiClientService } from '../http/api-client.service';

/**
 * Base CRUD Service
 * Provides common functionality for all CRUD operations
 */
@Injectable({
  providedIn: 'root'
})
export class BaseCrudService<T> {
  constructor(protected apiClient: ApiClientService) {}

  /**
   * Extracts array from API response
   * Handles various response formats
   */
  protected extractArray(response: any): T[] {
    if (Array.isArray(response)) {
      return response;
    }
    if (response?.data && Array.isArray(response.data)) {
      return response.data;
    }
    if (response?.content && Array.isArray(response.content)) {
      return response.content;
    }
    return [];
  }

  /**
   * Extract single item from response
   */
  protected extractItem(response: any): T | null {
    if (!response) {
      return null;
    }
    if (response?.data) {
      return response.data as T;
    }
    return response as T;
  }
}

