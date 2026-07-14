import { Component, Input, Output, EventEmitter, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FavoriteService } from '../../../core/services/favorite.service';
import { CartService } from '../../../core/services/cart.service';
import { ImageService, PLACEHOLDER_IMAGE } from '../../services/image.service';
import { PriceComponent } from '../price/price.component';

@Component({
  selector: 'app-product-card',
  standalone: true,
  imports: [CommonModule, RouterLink, PriceComponent],
  templateUrl: './product-card.component.html',
  styleUrl: './product-card.component.css'
})
export class ProductCardComponent {
  @Input() product: any;
  @Output() quickView = new EventEmitter<any>();
  @Output() addToCart = new EventEmitter<any>();

  Math = Math;

  private readonly favoriteService = inject(FavoriteService);
  private readonly cartService = inject(CartService);
  private readonly imageService = inject(ImageService);

  readonly isFavorite = computed(() => {
    return this.product ? this.favoriteService.isFavorite(this.product.id) : false;
  });

  private readonly loadedImageIds = new Set<number>();

  onQuickView(): void {
    this.quickView.emit(this.product);
  }

  onAddToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.cartService.addProduct(this.product, 1);
    this.addToCart.emit(this.product);
  }

  onToggleFavorite(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    this.favoriteService.toggleFavorite(this.product);
  }

   getDiscountPercentage(): number {
     if (!this.product?.originalPrice || !this.product?.cost) return 0;
     const discount = this.product.originalPrice - this.product.cost;
     return Math.round((discount / this.product.originalPrice) * 100);
   }

   getStockClass(): string {
     const stock = Number(this.product?.stock ?? 0);
     return stock > 0 ? 'in-stock' : 'out-of-stock';
   }

   getImageUrl(): string {
    const url = this.product?.imageUrl || this.product?.image;
    return this.imageService.getSafeImageUrl(url, PLACEHOLDER_IMAGE);
  }

  onImageLoaded(): void {
    const id = Number(this.product?.id);
    if (Number.isFinite(id)) {
      this.loadedImageIds.add(id);
    }
  }

  onImageError(event: Event): void {
    this.imageService.onImageError(event, PLACEHOLDER_IMAGE);
  }

  isImageLoaded(): boolean {
    const id = Number(this.product?.id);
    return Number.isFinite(id) ? this.loadedImageIds.has(id) : false;
  }
}
