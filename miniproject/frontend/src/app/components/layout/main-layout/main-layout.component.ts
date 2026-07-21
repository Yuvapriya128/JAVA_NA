import { Component, DestroyRef, inject, signal } from '@angular/core';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { BreadcrumbComponent } from '../../shared/breadcrumb/breadcrumb.component';
import { FooterComponent } from '../footer/footer.component';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { MobileBottomNavComponent } from '../mobile-bottom-nav/mobile-bottom-nav.component';

@Component({
  selector: 'app-main-layout',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, SidebarComponent, FooterComponent, BreadcrumbComponent, MobileBottomNavComponent],
  templateUrl: './main-layout.component.html',
  styleUrls: ['./main-layout.component.css']})
export class MainLayoutComponent {
  private readonly destroyRef = inject(DestroyRef);

  readonly isCollapsed = signal(false);
  readonly showMobileMenu = signal(false);
  readonly currentTitle = signal('Dashboard');

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {
    this.router.events.pipe(
      filter((event) => event instanceof NavigationEnd),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.currentTitle.set(this.getRouteTitle(this.route) || 'Dashboard');
      this.showMobileMenu.set(false);
    });
  }

  toggleSidebar(): void {
    if (window.innerWidth < 992) {
      this.showMobileMenu.update((state) => !state);
      return;
    }
    this.isCollapsed.update((state) => !state);
  }

  closeMobileMenu(): void {
    this.showMobileMenu.set(false);
  }

  private getRouteTitle(route: ActivatedRoute): string | null {
    let child = route.firstChild;
    while (child?.firstChild) {
      child = child.firstChild;
    }
    return child?.snapshot.data['title'] || null;
  }
}
