import { Component, OnInit, OnDestroy, ChangeDetectorRef, inject, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { FavoriteService } from '../../../../core/services/favorite.service';
import { CartService } from '../../../../core/services/cart.service';
import { ImageService, PLACEHOLDER_IMAGE } from '../../../../shared/services/image.service';
import { ProductResponseDTO } from '../../../../core/dto/product/product.dto';
import { LoadingComponent } from '../../../../shared/components/loading/loading.component';
import { PriceComponent } from '../../../../shared/components/price/price.component';
import { finalize, distinctUntilChanged } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-product-view',
  standalone: true,
  imports: [CommonModule, LoadingComponent, PriceComponent],
  templateUrl: './product-view.component.html',
  styleUrl: './product-view.component.css'
})
export class ProductViewComponent implements OnInit, OnDestroy {
  product: ProductResponseDTO | null = null;
  isLoading = true;
  errorMessage = '';
  private destroy$ = new Subject<void>();

  private readonly productService = inject(ProductService);
  private readonly favoriteService = inject(FavoriteService);
  private readonly cartService = inject(CartService);
  private readonly imageService = inject(ImageService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly cdr = inject(ChangeDetectorRef);

  readonly isFavorite = computed(() => {
    return this.product ? this.favoriteService.isFavorite(this.product.id) : false;
  });

  ngOnInit(): void {
    console.log('[ProductDetailsComponent] View Component Loaded');

    // Subscribe to route param changes (not just snapshot)
    this.route.paramMap
      .pipe(
        distinctUntilChanged((prev, curr) => prev.get('id') === curr.get('id')),
        takeUntil(this.destroy$)
      )
      .subscribe(params => {
        const id = params.get('id');
        console.log('[ProductDetailsComponent] Route param changed - ID:', id);

        // Validate route parameter
        if (!id || isNaN(+id) || +id <= 0) {
          console.warn('[ProductDetailsComponent] Invalid ID:', id);
          this.isLoading = false;
          this.errorMessage = 'Invalid product ID';
          this.cdr.detectChanges();
          return;
        }

        this.loadProduct(parseInt(id));
      });
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProduct(id: number): void {
    console.log('[ProductDetailsComponent] Calling getById:', id);
    this.isLoading = true;
    this.productService.getProductById(id)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (product) => {
          console.log('[ProductDetailsComponent] API Response:', product);
          this.product = product as ProductResponseDTO;
          this.errorMessage = '';
          console.log('[ProductDetailsComponent] Assigned Object:', this.product);
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[ProductDetailsComponent] API Error:', error);
          this.product = null;

          if (error.status === 404) {
            this.errorMessage = 'Product not found';
          } else if (error.status === 403) {
            this.errorMessage = 'Access denied';
          } else {
            this.errorMessage = 'Error loading product: ' + (error?.error?.message || error?.message || 'Unknown error');
          }
          this.cdr.detectChanges();
        }
      });
  }

  onBack(): void {
    this.router.navigate(['/products']);
  }

  onEdit(): void {
    if (this.product) {
      console.log('[ProductDetailsComponent] Clicked Edit', this.product.id);
      this.router.navigate(['/products/edit', this.product.id]);
    }
  }

   onDelete(): void {
     if (this.product && confirm('Are you sure you want to delete this product?')) {
       this.productService.deleteProduct(this.product.id).pipe(
         takeUntil(this.destroy$)
       ).subscribe({
         next: () => {
           this.router.navigate(['/products']);
         },
         error: (error) => console.error('Error deleting product:', error)
       });
     }
   }

   onImageError(event: Event): void {
    this.imageService.onImageError(event, PLACEHOLDER_IMAGE);
  }

  onAddToCart(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.product) {
      this.cartService.addProduct(this.product, 1);
    }
  }

  onToggleFavorite(event: Event): void {
    event.preventDefault();
    event.stopPropagation();
    if (this.product) {
      this.favoriteService.toggleFavorite(this.product);
    }
  }

  isAdmin(): boolean {
    // Check if running in admin context - can be enhanced with role checking
    return false;
  }
}


