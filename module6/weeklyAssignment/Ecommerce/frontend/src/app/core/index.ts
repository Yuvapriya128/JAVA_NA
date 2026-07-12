/**
 * Core Module Barrel Exports
 * Provides unified API for all core services, guards, interceptors, DTOs, and models
 */

// ============================================
// Auth Module - Single Source of Truth
// ============================================
export * from './auth/auth.service';
export * from './auth/jwt.service';
export * from './auth/token-storage.service';
export * from './auth/auth-state.service';
export * from './auth/auth.guard';
export * from './auth/role.guard';
export * from './auth/permission.guard';
export * from './auth/auth.interceptor';
export * from './auth/error.interceptor';
export * from './auth/dto/auth.dto';
export * from './auth/constants/roles.constants';
export * from './auth/constants/permissions.constants';
export { type AuthUser } from './auth/auth-state.service';
export { type AuthStatus } from './auth/auth.service';

// ============================================
// HTTP
// ============================================
export * from './http/api-client.service';
export * from './http/endpoints';

// ============================================
// DTOs - One Source of Truth
// ============================================
export * from './dto/customer/customer.dto';
export * from './dto/product/product.dto';
export * from './dto/order/order.dto';
export * from './dto/order-item/orderitem.dto';

// ============================================
// Models - One Source of Truth
// ============================================
export * from './models/customer.model';
export * from './models/product.model';
export * from './models/order.model';
export * from './models/order-item.model';
export * from './models/user.model';

// ============================================
// Services
// ============================================
export * from './services/customer.service';
export * from './services/product.service';
export * from './services/order.service';
export * from './services/order-item.service';
export * from './services/cart.service';
export * from './services/favorite.service';
export * from './services/order-refresh.service';
export * from './services/base.service';


// ============================================
// Utilities
// ============================================
export * from './utils/currency.util';
export * from './utils/date.util';

