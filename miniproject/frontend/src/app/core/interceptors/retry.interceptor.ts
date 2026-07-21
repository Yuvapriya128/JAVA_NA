import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { retry, timer } from 'rxjs';

const MAX_RETRIES = 2;

export const retryInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.method !== 'GET') {
    return next(req);
  }

  return next(req).pipe(
    retry({
      count: MAX_RETRIES,
      delay: (error: HttpErrorResponse, retryCount) => {
        if (!isRetryable(error)) {
          throw error;
        }
        return timer(retryCount * 700);
      }
    })
  );
};

function isRetryable(error: HttpErrorResponse): boolean {
  return error.status === 0 || error.status === 500 || error.status === 502 || error.status === 503 || error.status === 504;
}
