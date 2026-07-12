import { Injectable } from '@angular/core';
import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpErrorResponse,
  HttpResponse
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, tap } from 'rxjs/operators';
import { AuthService } from './auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private readonly authService: AuthService) {}

  intercept(request: HttpRequest<unknown>, next: HttpHandler): Observable<HttpEvent<unknown>> {
    const token = this.authService.getToken();

    console.log('[AuthInterceptor] Intercepting request:', request.url);
    console.log('[AuthInterceptor] Token exists:', !!token);
    if (token) {
      console.log('[AuthInterceptor] Token length:', token.length);
      console.log('[AuthInterceptor] Token preview (first 20 chars):', token.substring(0, 20) + '...');
    }

    if (token && !request.url.includes('/auth/login')) {
      console.log('[AuthInterceptor] Adding Authorization header for:', request.url);
      request = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
      console.log('[AuthInterceptor] Headers after clone:', request.headers.keys());
      console.log('[AuthInterceptor] Authorization header:', request.headers.get('Authorization') ? 'Present' : 'Missing');
    } else {
      console.log('[AuthInterceptor] Skipping Authorization header');
      if (request.url.includes('/auth/login')) {
        console.log('[AuthInterceptor] Reason: Login endpoint excluded');
      }
      if (!token) {
        console.log('[AuthInterceptor] Reason: No token available');
      }
    }

    return next.handle(request).pipe(
      tap((event) => {
        if (event instanceof HttpResponse) {
          console.log('[AuthInterceptor] Response:', event.status, request.url);
        }
      }),
      catchError((error: HttpErrorResponse) => {
        console.error('[AuthInterceptor] Error Response Status:', error.status);
        console.error('[AuthInterceptor] Error URL:', request.url);
        console.error('[AuthInterceptor] Error body:', error.error);
        if (error.status === 401) {
          console.log('[AuthInterceptor] 401 Detected - Logging out');
          this.authService.logout();
        }
        return throwError(() => error);
      })
    );
  }
}

