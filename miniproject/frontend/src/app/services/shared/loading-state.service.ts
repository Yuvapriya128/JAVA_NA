import { Injectable, computed, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class LoadingStateService {
  private readonly pendingRequests = signal(0);
  private readonly navigationLoading = signal(false);

  readonly requestCount = computed(() => this.pendingRequests());
  readonly isLoading = computed(() => this.pendingRequests() > 0 || this.navigationLoading());
  readonly message = computed(() => {
    if (this.navigationLoading()) {
      return 'Loading page... Please wait...';
    }
    if (this.pendingRequests() > 0) {
      return 'Loading data... Please wait...';
    }
    return '';
  });

  requestStarted(): void {
    this.pendingRequests.update((count) => count + 1);
  }

  requestFinished(): void {
    this.pendingRequests.update((count) => Math.max(0, count - 1));
  }

  setNavigationLoading(isLoading: boolean): void {
    this.navigationLoading.set(isLoading);
  }
}
