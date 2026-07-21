import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, NavigationEnd, Router, RouterOutlet } from '@angular/router';
import { filter } from 'rxjs';
import { BreadcrumbComponent } from '../../shared/breadcrumb/breadcrumb.component';
import { FooterComponent } from '../footer/footer.component';
import { NavbarComponent } from '../navbar/navbar.component';
import { SidebarComponent } from '../sidebar/sidebar.component';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [CommonModule, RouterOutlet, NavbarComponent, SidebarComponent, FooterComponent, BreadcrumbComponent],
  templateUrl: './shell.component.html',
  styleUrls: ['./shell.component.css']})
export class ShellComponent {
  private readonly destroyRef = inject(DestroyRef);

  isCollapsed = false;
  showMobileMenu = false;
  currentTitle = 'Dashboard';

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {
    this.router.events.pipe(
      filter((e) => e instanceof NavigationEnd),
      takeUntilDestroyed(this.destroyRef)
    ).subscribe(() => {
      this.currentTitle = this.getRouteTitle(this.route) || 'Dashboard';
      this.showMobileMenu = false;
    });
  }

  toggleSidebar(): void {
    if (window.innerWidth < 992) {
      this.showMobileMenu = !this.showMobileMenu;
      return;
    }
    this.isCollapsed = !this.isCollapsed;
  }

  private getRouteTitle(route: ActivatedRoute): string | null {
    let child = route.firstChild;
    while (child?.firstChild) {
      child = child.firstChild;
    }
    return child?.snapshot.data['title'] || null;
  }
}

