import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { ProductResponseDTO } from '../dto/product/product.dto';
import { UserScopedStorageService } from './user-scoped-storage.service';

export interface CartItem {
  product: ProductResponseDTO;
  quantity: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private readonly storagePrefix = 'cart';
  private readonly userScopedStorage = inject(UserScopedStorageService);
  private readonly cartItemsSignal = signal<CartItem[]>(this.loadCart());

  readonly cartItems = this.cartItemsSignal.asReadonly();
  readonly cartCount = computed(() => this.cartItemsSignal().reduce((sum, item) => sum + Number(item.quantity || 0), 0));
  readonly subtotal = computed(() => this.cartItemsSignal().reduce((sum, item) => sum + (Number(item.product?.cost || 0) * Number(item.quantity || 0)), 0));

  constructor() {
    effect(() => {
      this.userScopedStorage.getCurrentUserStorageKey(this.storagePrefix);
      this.reloadCart();
    }, { allowSignalWrites: true });
  }

  addProduct(product: ProductResponseDTO, quantity = 1): void {
    const productId = Number(product?.id);
    const qty = Number(quantity);
    if (!Number.isFinite(productId) || productId <= 0 || !Number.isFinite(qty) || qty <= 0) {
      return;
    }

    const next = [...this.cartItemsSignal()];
    const existing = next.find((entry) => Number(entry.product?.id) === productId);

    if (existing) {
      existing.quantity = Number(existing.quantity || 0) + qty;
    } else {
      next.push({ product, quantity: qty });
    }

    this.setCart(next);
  }

  increaseQuantity(productId: number): void {
    const targetId = Number(productId);
    if (!Number.isFinite(targetId) || targetId <= 0) {
      return;
    }

    const next = [...this.cartItemsSignal()];
    const existing = next.find((entry) => Number(entry.product?.id) === targetId);
    if (!existing) {
      return;
    }

    existing.quantity = Number(existing.quantity || 0) + 1;
    this.setCart(next);
  }

  decreaseQuantity(productId: number): void {
    const targetId = Number(productId);
    if (!Number.isFinite(targetId) || targetId <= 0) {
      return;
    }

    const next = [...this.cartItemsSignal()];
    const existing = next.find((entry) => Number(entry.product?.id) === targetId);
    if (!existing) {
      return;
    }

    if (Number(existing.quantity) <= 1) {
      this.removeProduct(targetId);
      return;
    }

    existing.quantity = Number(existing.quantity || 0) - 1;
    this.setCart(next);
  }

  removeProduct(productId: number): void {
    const targetId = Number(productId);
    if (!Number.isFinite(targetId) || targetId <= 0) {
      return;
    }

    const next = this.cartItemsSignal().filter((entry) => Number(entry.product?.id) !== targetId);
    this.setCart(next);
  }

  clearCart(): void {
    this.setCart([]);
  }

  reloadCart(): void {
    this.cartItemsSignal.set(this.loadCart());
  }

  private setCart(items: CartItem[]): void {
    this.cartItemsSignal.set(items);
    this.persist(items);
  }

  private loadCart(): CartItem[] {
    const cartKey = this.userScopedStorage.getCurrentUserStorageKey(this.storagePrefix);
    if (!cartKey) {
      return [];
    }

    try {
      const raw = globalThis.localStorage?.getItem(cartKey);
      if (raw === null) {
        this.persist([]);
        return [];
      }

      const parsed = raw ? JSON.parse(raw) : [];
      if (!Array.isArray(parsed)) {
        return [];
      }

      return parsed
        .map((entry) => ({
          product: entry?.product as ProductResponseDTO,
          quantity: Number(entry?.quantity ?? 1)
        }))
        .filter((entry) => !!entry.product && Number(entry.product.id) > 0 && Number(entry.quantity) > 0);
    } catch {
      return [];
    }
  }

  private persist(items: CartItem[]): void {
    const cartKey = this.userScopedStorage.getCurrentUserStorageKey(this.storagePrefix);
    if (!cartKey) {
      return;
    }

    try {
      globalThis.localStorage?.setItem(cartKey, JSON.stringify(items));
    } catch {
      // Ignore storage exceptions in restricted browser modes.
    }
  }
}
