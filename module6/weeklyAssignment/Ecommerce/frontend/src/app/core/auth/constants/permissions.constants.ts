export const PERMISSIONS = {
  DASHBOARD_READ: 'dashboard:read',

  CUSTOMER_CREATE: 'customer:create',
  CUSTOMER_READ: 'customer:read',
  CUSTOMER_UPDATE: 'customer:update',
  CUSTOMER_DELETE: 'customer:delete',
  CUSTOMER_ROLE_UPDATE: 'customer:role:update',

  PRODUCT_CREATE: 'product:create',
  PRODUCT_READ: 'product:read',
  PRODUCT_UPDATE: 'product:update',
  PRODUCT_DELETE: 'product:delete',

  ORDER_CREATE: 'order:create',
  ORDER_READ: 'order:read',
  ORDER_UPDATE: 'order:update',
  ORDER_DELETE: 'order:delete',
  ORDER_CANCEL_OWN: 'order:cancel:own',

  ORDER_ITEM_CREATE: 'order-item:create',
  ORDER_ITEM_READ: 'order-item:read',
  ORDER_ITEM_UPDATE: 'order-item:update',
  ORDER_ITEM_DELETE: 'order-item:delete',

  PROFILE_READ: 'profile:read',
  PROFILE_UPDATE_SELF: 'profile:update:self',

  REPORT_READ: 'report:read',
  SETTINGS_READ: 'settings:read',
  SETTINGS_UPDATE: 'settings:update'
} as const;

export type Permission = (typeof PERMISSIONS)[keyof typeof PERMISSIONS];

export const ROLE_PERMISSIONS: Record<string, Permission[]> = {
  ADMIN: Object.values(PERMISSIONS),
  MANAGER: [
    PERMISSIONS.DASHBOARD_READ,
    PERMISSIONS.PRODUCT_CREATE,
    PERMISSIONS.PRODUCT_READ,
    PERMISSIONS.PRODUCT_UPDATE,
    PERMISSIONS.PRODUCT_DELETE,
    PERMISSIONS.ORDER_CREATE,
    PERMISSIONS.ORDER_READ,
    PERMISSIONS.ORDER_UPDATE,
    PERMISSIONS.ORDER_DELETE,
    PERMISSIONS.ORDER_ITEM_CREATE,
    PERMISSIONS.ORDER_ITEM_READ,
    PERMISSIONS.ORDER_ITEM_UPDATE,
    PERMISSIONS.ORDER_ITEM_DELETE,
    PERMISSIONS.PROFILE_READ,
    PERMISSIONS.PROFILE_UPDATE_SELF
  ],
  USER: [
    PERMISSIONS.DASHBOARD_READ,
    PERMISSIONS.ORDER_CREATE,
    PERMISSIONS.ORDER_READ,
    PERMISSIONS.ORDER_CANCEL_OWN,
    PERMISSIONS.PROFILE_READ,
    PERMISSIONS.PROFILE_UPDATE_SELF
  ]
};
