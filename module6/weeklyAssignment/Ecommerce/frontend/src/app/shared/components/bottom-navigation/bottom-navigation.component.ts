import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NavigationEnd, Router, RouterLink, RouterLinkActive } from '@angular/router';
import { toSignal } from '@angular/core/rxjs-interop';
import { filter, map, startWith } from 'rxjs/operators';
import { type AppNavigationItem } from '../../navigation/navigation.config';
import { NavigationService } from '../../navigation/navigation.service';

@Component({
  selector: 'app-bottom-navigation',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './bottom-navigation.component.html',
  styleUrl: './bottom-navigation.component.css',
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BottomNavigationComponent {
  private readonly router = inject(Router);
  private readonly navigationService = inject(NavigationService);

  readonly isMoreOpen = signal(false);
  readonly mobileItems = this.navigationService.mobileItems;
  readonly currentUrl = toSignal(
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      map(() => this.router.url),
      startWith(this.router.url)
    ),
    { initialValue: this.router.url }
  );

  readonly primaryItems = computed(() => {
    const items = this.mobileItems();
    if (items.length <= 5) {
      return items;
    }

    return items.slice(0, 4);
  });

  readonly overflowItems = computed(() => {
    const items = this.mobileItems();
    if (items.length <= 5) {
      return [] as AppNavigationItem[];
    }

    return items.slice(4);
  });

  readonly hasOverflow = computed(() => this.overflowItems().length > 0);
  readonly isOverflowActive = computed(() => {
    const url = this.normalizeUrl(this.currentUrl());
    return this.overflowItems().some((item) => this.isRouteActive(item.route, url));
  });

  toggleMoreSheet(): void {
    this.isMoreOpen.update((value) => !value);
  }

  closeMoreSheet(): void {
    this.isMoreOpen.set(false);
  }

  onNavigationClick(): void {
    this.closeMoreSheet();
  }

  getBadgeCount(item: AppNavigationItem): number {
    return this.navigationService.getBadgeCount(item);
  }

  private isRouteActive(route: string, normalizedUrl: string): boolean {
    return normalizedUrl === route || normalizedUrl.startsWith(`${route}/`);
  }

  private normalizeUrl(url: string): string {
    return (url || '').split('?')[0].split('#')[0];
  }
}
