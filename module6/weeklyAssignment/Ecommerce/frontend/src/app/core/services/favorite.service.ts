import { Injectable, computed, signal } from '@angular/core';
import { ProductResponseDTO } from '../dto/product/product.dto';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private readonly favoritesKey = 'ecommerce_favorites';
  private readonly favoritesSignal = signal<ProductResponseDTO[]>(this.loadFavorites());

  readonly favorites = this.favoritesSignal.asReadonly();
  readonly favoriteIds = computed(() => new Set(this.favoritesSignal().map((item) => Number(item.id)).filter((id) => Number.isFinite(id) && id > 0)));

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
    try {
      const raw = globalThis.localStorage?.getItem(this.favoritesKey);
      const parsed = raw ? JSON.parse(raw) : [];
      return Array.isArray(parsed) ? (parsed as ProductResponseDTO[]) : [];
    } catch {
      return [];
    }
  }

  private persist(next: ProductResponseDTO[]): void {
    try {
      globalThis.localStorage?.setItem(this.favoritesKey, JSON.stringify(next));
    } catch {
      // Ignore storage exceptions in restricted browser modes.
    }
  }
}

