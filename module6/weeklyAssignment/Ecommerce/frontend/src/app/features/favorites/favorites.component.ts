import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductResponseDTO } from '../../core/dto/product/product.dto';
import { CartService } from '../../core/services/cart.service';
import { FavoriteService } from '../../core/services/favorite.service';

@Component({
  selector: 'app-favorites',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './favorites.component.html',
  styleUrl: './favorites.component.css'
})
export class FavoritesComponent {
  private readonly favoriteService = inject(FavoriteService);
  private readonly cartService = inject(CartService);
  private toastTimer: ReturnType<typeof setTimeout> | null = null;

  readonly favorites = this.favoriteService.favorites;
  showAddToCartToast = false;


  removeFavorite(productId: number): void {
    this.favoriteService.removeFavorite(productId);
  }

  clearAll(): void {
    this.favoriteService.clearFavorites();
  }

  addToCart(product: ProductResponseDTO): void {
    this.cartService.addProduct(product, 1);
    this.showAddToCartToast = true;
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
    }
    this.toastTimer = setTimeout(() => {
      this.showAddToCartToast = false;
      this.toastTimer = null;
    }, 2200);
  }
}

