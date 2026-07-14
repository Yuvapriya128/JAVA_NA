import { Injectable, computed, effect, inject, signal } from '@angular/core';
import { ProductResponseDTO } from '../dto/product/product.dto';
import { UserScopedStorageService } from './user-scoped-storage.service';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private readonly storagePrefix = 'favorites';
  private readonly userScopedStorage = inject(UserScopedStorageService);
  private readonly favoritesSignal = signal<ProductResponseDTO[]>(this.loadFavorites());

  readonly favorites = this.favoritesSignal.asReadonly();
  readonly favoriteIds = computed(() => new Set(this.favoritesSignal().map((item) => Number(item.id)).filter((id) => Number.isFinite(id) && id > 0)));

  constructor() {
    effect(() => {
      this.userScopedStorage.getCurrentUserStorageKey(this.storagePrefix);
      this.reloadFavorites();
    }, { allowSignalWrites: true });
  }

  isFavorite(productId: number): boolean {
    return this.favoriteIds().has(Number(productId));
  }

  toggleFavorite(product: ProductResponseDTO): void {
    const productId = Number(product?.id);
    if (!Number.isFinite(productId) || productId <= 0) {
      return;
    }

    const next = [...this.favoritesSignal()];
    const existingIndex = next.findIndex((item) => Number(item.id) === productId);

    if (existingIndex >= 0) {
      next.splice(existingIndex, 1);
    } else {
      next.push(product);
    }

    this.setFavorites(next);
  }

  removeFavorite(productId: number): void {
    const targetId = Number(productId);
    if (!Number.isFinite(targetId) || targetId <= 0) {
      return;
    }

    const next = this.favoritesSignal().filter((item) => Number(item.id) !== targetId);
    this.setFavorites(next);
  }

  clearFavorites(): void {
    this.setFavorites([]);
  }

  reloadFavorites(): void {
    this.favoritesSignal.set(this.loadFavorites());
  }

  private setFavorites(next: ProductResponseDTO[]): void {
    this.favoritesSignal.set(next);
    this.persist(next);
  }

  private loadFavorites(): ProductResponseDTO[] {
    const favoritesKey = this.userScopedStorage.getCurrentUserStorageKey(this.storagePrefix);
    if (!favoritesKey) {
      return [];
    }

    try {
      const raw = globalThis.localStorage?.getItem(favoritesKey);
      if (raw === null) {
        this.persist([]);
        return [];
      }

      const parsed = raw ? JSON.parse(raw) : [];
      return Array.isArray(parsed) ? (parsed as ProductResponseDTO[]) : [];
    } catch {
      return [];
    }
  }

  private persist(next: ProductResponseDTO[]): void {
    const favoritesKey = this.userScopedStorage.getCurrentUserStorageKey(this.storagePrefix);
    if (!favoritesKey) {
      return;
    }

    try {
      globalThis.localStorage?.setItem(favoritesKey, JSON.stringify(next));
    } catch {
      // Ignore storage exceptions in restricted browser modes.
    }
  }
}
