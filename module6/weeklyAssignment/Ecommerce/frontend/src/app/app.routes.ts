import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';
import { ROLES } from './core/auth/constants/roles.constants';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    redirectTo: 'auth/login',
    pathMatch: 'full'
  },
  {
    path: 'auth',
    loadChildren: () => import('./features/auth/auth.routes').then(m => m.authRoutes)
  },
  // Protected routes with a single Admin layout shell
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard, roleGuard([ROLES.ADMIN, ROLES.MANAGER, ROLES.USER])],
    children: [
      {
        path: 'dashboard',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.MANAGER, ROLES.USER])],
        loadChildren: () => import('./features/dashboard/dashboard.routes').then(m => m.dashboardRoutes)
      },
      {
        path: 'products',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.MANAGER, ROLES.USER])],
        loadChildren: () => import('./features/products/product.routes').then(m => m.productRoutes)
      },
      {
        path: 'customers',
        canActivate: [roleGuard([ROLES.ADMIN])],
        loadChildren: () => import('./features/customers/customer.routes').then(m => m.customerRoutes)
      },
      {
        path: 'orders',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.MANAGER, ROLES.USER])],
        loadChildren: () => import('./features/orders/order.routes').then(m => m.orderRoutes)
      },
      {
        path: 'order-items',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.MANAGER])],
        loadChildren: () => import('./features/order-items/order-item.routes').then(m => m.orderItemRoutes)
      },
      {
        path: 'orderitems',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.MANAGER])],
        loadChildren: () => import('./features/order-items/order-item.routes').then(m => m.orderItemRoutes)
      },
      {
        path: 'favorites',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.USER])],
        loadChildren: () => import('./features/favorites/favorites.routes').then(m => m.favoritesRoutes)
      },
      {
        path: 'cart',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.USER])],
        loadChildren: () => import('./features/cart/cart.routes').then(m => m.cartRoutes)
      },
      {
        path: 'checkout',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.USER])],
        loadChildren: () => import('./features/checkout/checkout.routes').then(m => m.checkoutRoutes)
      },
      {
        path: 'profile',
        canActivate: [roleGuard([ROLES.ADMIN, ROLES.MANAGER, ROLES.USER])],
        loadChildren: () => import('./features/profile/profile.routes').then(m => m.profileRoutes)
      }
    ]
  },
  {
    path: 'unauthorized',
    loadComponent: () => import('./features/errors/unauthorized.component').then(m => m.UnauthorizedComponent)
  },
  {
    path: 'access-denied',
    loadComponent: () => import('./features/errors/access-denied.component').then(m => m.AccessDeniedComponent)
  },
  {
    path: 'not-found',
    loadComponent: () => import('./features/errors/not-found.component').then(m => m.NotFoundComponent)
  },
  {
    path: '**',
    redirectTo: 'not-found'
  }
];
