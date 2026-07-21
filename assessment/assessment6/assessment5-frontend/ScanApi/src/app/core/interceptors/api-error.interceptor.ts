import {
  HttpErrorResponse,
  HttpInterceptorFn,
} from '@angular/common/http';
import { throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

function getUserFriendlyMessage(error: HttpErrorResponse): string {
  if (error.status === 0) {
    return 'Cannot reach backend server. Please make sure API is running at http://localhost:9090.';
  }

  const backendMessage =
    typeof error.error === 'string'
      ? error.error
      : (error.error?.message as string | undefined);

  if (backendMessage) {
    return backendMessage;
  }

  switch (error.status) {
    case 400:
      return 'Request is invalid. Please verify your input.';
    case 404:
      return 'Requested data was not found.';
    case 500:
      return 'Server error occurred. Please try again later.';
    default:
      return `Request failed (${error.status}). Please try again.`;
  }
}

export const apiErrorInterceptor: HttpInterceptorFn = (req, next) => {
  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      const userMessage = getUserFriendlyMessage(error);
      return throwError(() => new Error(userMessage));
    })
  );
};
