import { Component, DestroyRef, inject } from '@angular/core';
import { NavigationCancel, NavigationEnd, NavigationError, NavigationStart, Router, RouterOutlet } from '@angular/router';
import { ToastContainerComponent } from './components/shared/notifications/toast-container/toast-container.component';
import { ThemeService } from './services/shared/theme.service';
import { LoadingStateService } from './services/shared/loading-state.service';
import { GlobalLoadingComponent } from './components/shared/loading/global-loading.component';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ConfirmationModalComponent } from './components/shared/modals/confirmation-modal.component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, ToastContainerComponent, GlobalLoadingComponent, ConfirmationModalComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly themeService = inject(ThemeService);
  private readonly router = inject(Router);
  private readonly loadingState = inject(LoadingStateService);
  private readonly destroyRef = inject(DestroyRef);

  constructor() {
    this.themeService.initializeTheme();
    this.router.events
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((event) => {
        if (event instanceof NavigationStart) {
          this.loadingState.setNavigationLoading(true);
        }
        if (event instanceof NavigationEnd || event instanceof NavigationCancel || event instanceof NavigationError) {
          this.loadingState.setNavigationLoading(false);
        }
      });
  }
}
