import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { finalize } from 'rxjs';
import { LoadingStateService } from '../../services/shared/loading-state.service';

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  const loadingState = inject(LoadingStateService);
  loadingState.requestStarted();

  return next(req).pipe(finalize(() => loadingState.requestFinished()));
};
