import { HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';

/**
 * Creates a reusable HTTP error handler for service methods.
 */
export function handleHttpError(context: string): (error: HttpErrorResponse) => Observable<never> {
  void context;
  return (error: HttpErrorResponse): Observable<never> => {
    const message = resolveFriendlyMessage(error);
    return throwError(() => new Error(message));
  };
}

function resolveFriendlyMessage(error: HttpErrorResponse): string {
  const serverMessage =
    typeof error.error === 'string'
      ? error.error
      : String(error.error?.message || '').trim();

  if (serverMessage) {
    return serverMessage;
  }

  if (error.status === 0) {
    return 'Network connection issue. Please check your internet and try again.';
  }
  if (error.status === 400) {
    return 'Some input is invalid. Please review the form and retry.';
  }
  if (error.status === 401) {
    return 'Your session has expired. Please login again.';
  }
  if (error.status === 403) {
    return 'You do not have permission to perform this action.';
  }
  if (error.status === 404) {
    return 'Requested data was not found.';
  }
  if (error.status >= 500) {
    return 'Server is temporarily unavailable. Please try again shortly.';
  }
  return 'Unable to process the request right now. Please try again.';
}
