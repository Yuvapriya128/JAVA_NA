import { ROLES, type Role } from '../../core/auth/constants/roles.constants';

export type NavigationSection = 'main' | 'account';
export type NavigationBadgeKey = 'cart' | 'orders' | 'notifications';

export interface AppNavigationItem {
  id: string;
  label: string;
  icon: string;
  route: string;
  roles: Role[];
  section: NavigationSection;
  showOnDesktop: boolean;
  showOnMobile: boolean;
  desktopPriority: number;
  mobilePriority: number;
  exactMatch?: boolean;
  badgeKey?: NavigationBadgeKey;
}

export const APP_NAVIGATION_ITEMS: AppNavigationItem[] = [
  {
    id: 'dashboard',
    label: 'Dashboard',
    icon: 'bi-speedometer2',
    route: '/dashboard',
    roles: [ROLES.ADMIN, ROLES.MANAGER],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 10,
    mobilePriority: 10,
    exactMatch: true
  },
  {
    id: 'customers',
    label: 'Customers',
    icon: 'bi-people',
    route: '/customers',
    roles: [ROLES.ADMIN],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 20,
    mobilePriority: 20
  },
  {
    id: 'products',
    label: 'Products',
    icon: 'bi-box',
    route: '/products',
    roles: [ROLES.ADMIN, ROLES.MANAGER, ROLES.USER],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 30,
    mobilePriority: 30
  },
  {
    id: 'orders',
    label: 'Orders',
    icon: 'bi-bag',
    route: '/orders',
    roles: [ROLES.ADMIN, ROLES.MANAGER, ROLES.USER],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 40,
    mobilePriority: 40,
    badgeKey: 'orders'
  },
  {
    id: 'order-items',
    label: 'Order Items',
    icon: 'bi-list-check',
    route: '/order-items',
    roles: [ROLES.ADMIN, ROLES.MANAGER],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 50,
    mobilePriority: 90
  },
  {
    id: 'cart',
    label: 'Cart',
    icon: 'bi-cart3',
    route: '/cart',
    roles: [ROLES.USER],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 35,
    mobilePriority: 35,
    badgeKey: 'cart'
  },
  {
    id: 'favorites',
    label: 'Favorites',
    icon: 'bi-heart',
    route: '/favorites',
    roles: [ROLES.USER],
    section: 'main',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 37,
    mobilePriority: 37
  },
  {
    id: 'profile',
    label: 'Profile',
    icon: 'bi-person',
    route: '/profile',
    roles: [ROLES.ADMIN, ROLES.MANAGER, ROLES.USER],
    section: 'account',
    showOnDesktop: true,
    showOnMobile: true,
    desktopPriority: 100,
    mobilePriority: 50,
    exactMatch: true
  }
];
