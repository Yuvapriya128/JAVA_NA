import { TestBed } from '@angular/core/testing';
import {
  HttpTestingController,
  provideHttpClientTesting,
} from '@angular/common/http/testing';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpClient } from '@angular/common/http';
import { apiErrorInterceptor } from './api-error.interceptor';

describe('apiErrorInterceptor', () => {
  let httpClient: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([apiErrorInterceptor])),
        provideHttpClientTesting(),
      ],
    });

    httpClient = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should convert network errors to friendly message', () => {
    let actualMessage = '';

    httpClient.get('http://localhost:9090/scan').subscribe({
      next: () => {
        throw new Error('Expected an error');
      },
      error: (error: Error) => {
        actualMessage = error.message;
      },
    });

    const req = httpMock.expectOne('http://localhost:9090/scan');
    req.error(new ProgressEvent('error'), { status: 0 });

    expect(actualMessage).toContain('Cannot reach backend server');
  });

  it('should prefer backend message when available', () => {
    let actualMessage = '';

    httpClient.get('http://localhost:9090/scan/99').subscribe({
      next: () => {
        throw new Error('Expected an error');
      },
      error: (error: Error) => {
        actualMessage = error.message;
      },
    });

    const req = httpMock.expectOne('http://localhost:9090/scan/99');
    req.flush({ message: 'Scan not found' }, { status: 404, statusText: 'Not Found' });

    expect(actualMessage).toBe('Scan not found');
  });
});
