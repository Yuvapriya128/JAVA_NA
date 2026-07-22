import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

/**
 * Global HTTP error handling. Converts backend/network failures into
 * user-friendly notifications while re-throwing so callers can still react.
 */
export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const notifications = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      notifications.error(resolveMessage(error));
      return throwError(() => error);
    }),
  );
};

function resolveMessage(error: HttpErrorResponse): string {
  // Client-side / network error.
  if (error.status === 0) {
    return 'Cannot reach the server. Please check your connection and try again.';
  }

  switch (error.status) {
    case 400:
      return backendMessage(error) ?? 'Bad request. Please review the submitted data.';
    case 403:
      return 'You are not allowed to perform this action.';
    case 404:
      return backendMessage(error) ?? 'The requested resource was not found.';
    default:
      return backendMessage(error) ?? `Unexpected error (status ${error.status}).`;
  }
}

/** Attempts to extract a human-readable message from the backend error body. */
function backendMessage(error: HttpErrorResponse): string | null {
  const body = error.error;
  if (typeof body === 'string' && body.trim().length > 0) {
    return body;
  }
  if (body && typeof body === 'object' && typeof body.message === 'string') {
    return body.message;
  }
  return null;
}
