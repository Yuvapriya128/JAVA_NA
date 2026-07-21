import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { TokenStorageService } from '../../services/auth/token-storage.service';
import { ToastNotificationService } from '../../services/shared/toast-notification.service';

export const httpErrorInterceptor: HttpInterceptorFn = (req, next) => {
  const router = inject(Router);
  const tokenStorage = inject(TokenStorageService);
  const toastService = inject(ToastNotificationService);
  const isLoginRequest = req.url.includes('/auth/login');
  const retryAction = {
    actionLabel: 'Retry',
    onAction: () => window.location.reload()
  };

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      console.error('[HTTP ERROR]', {
        method: req.method,
        url: req.url,
        status: error.status,
        backendPayload: error.error
      });

      switch (error.status) {
        case 400:
          toastService.show(getValidationMessage(error), 'warning');
          break;
        case 401:
          if (isLoginRequest) {
            // Login page handles credential/inactive-account messaging directly.
          } else {
            tokenStorage.clearSession();
            toastService.show('Your session has expired. Please sign in again.', 'warning', { timeoutMs: 5000 });
            router.navigate(['/login']);
          }
          break;
        case 403:
          toastService.show('Access denied. You do not have permission for this action.', 'danger');
          router.navigate(['/access-denied']);
          break;
        case 404:
          toastService.show('The requested resource was not found. Please check the URL or try again.', 'info');
          break;
        case 500:
          toastService.show('Server is temporarily unavailable. Please try again in a moment.', 'danger', retryAction);
          break;
        case 503:
        case 504:
          toastService.show('Service is currently unavailable. Please try again shortly.', 'warning', retryAction);
          break;
        default:
          if (error.status === 0) {
            toastService.show(getNetworkMessage(error), 'warning', retryAction);
          } else {
            toastService.show('Something went wrong. Please try again later.', 'danger', retryAction);
          }
          break;
      }

      return throwError(() => error);
    })
  );
};

function getMessage(error: HttpErrorResponse, fallback: string): string {
  if (typeof error.error === 'string') {
    return error.error;
  }

  return error.error?.message || error.message || fallback;
}

function getValidationMessage(error: HttpErrorResponse): string {
  const normalized = getMessage(error, 'Some fields are invalid. Please check and submit again.');
  const fieldErrors = (error.error as { errors?: Record<string, string | string[]> } | null)?.errors;
  if (!fieldErrors || typeof fieldErrors !== 'object') {
    return normalized;
  }

  const firstField = Object.keys(fieldErrors)[0];
  if (!firstField) {
    return normalized;
  }
  const fieldMessage = fieldErrors[firstField];
  if (Array.isArray(fieldMessage)) {
    return fieldMessage[0] || normalized;
  }
  return fieldMessage || normalized;
}

function getNetworkMessage(error: HttpErrorResponse): string {
  const raw = getMessage(error, '').toLowerCase();
  if (raw.includes('timeout')) {
    return 'Request timed out. Please check your connection and try again.';
  }
  return 'Network issue detected. Please check your internet connection and retry.';
}
