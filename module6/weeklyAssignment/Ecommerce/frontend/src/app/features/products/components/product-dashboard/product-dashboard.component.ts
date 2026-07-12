import { Component, OnInit, ChangeDetectorRef, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { ProductService } from '../../../../core/services/product.service';
import { PaginationComponent } from '../../../../shared/components/pagination/pagination.component';
import { SearchComponent } from '../../../../shared/components/search/search.component';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { PERMISSIONS } from '../../../../core/auth/constants/permissions.constants';
import { ProductCardComponent } from '../../../../shared/components/product-card/product-card.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { SkeletonComponent } from '../../../../shared/components/skeleton/skeleton.component';
import { ToastComponent } from '../../../../shared/components/toast/toast.component';
import { ConfirmDialogComponent } from '../../../../shared/components/dialogs/confirm-dialog.component';

@Component({
  selector: 'app-product-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    PaginationComponent,
    SearchComponent,
    ProductCardComponent,
    EmptyStateComponent,
    SkeletonComponent,
    ToastComponent,
    ConfirmDialogComponent
  ],
  templateUrl: './product-dashboard.component.html',
  styleUrl: './product-dashboard.component.css'
})
export class ProductDashboardComponent implements OnInit {
  allProducts: any[] = [];
  filteredProducts: any[] = [];
  products: any[] = [];
  isLoading = true;
  currentPage = 0;
  totalPages = 1;
  pageSize = 12;

  searchTerm = '';
  selectedCategory = 'all';
  selectedBrand = 'all';
  sortBy: 'featured' | 'price-asc' | 'price-desc' | 'name-asc' = 'featured';

  showDeleteDialog = false;
  deletingProductId: number | null = null;

  toastShow = false;
  toastTitle = '';
  toastMessage = '';
  toastVariant: 'success' | 'danger' | 'warning' | 'info' = 'info';

  private readonly authState = inject(AuthStateService);

  constructor(
    private readonly productService: ProductService,
    private readonly changeDetectorRef: ChangeDetectorRef,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.loadProducts();
  }

  get searchSuggestions(): string[] {
    return this.allProducts
      .flatMap((product) => [product?.name, product?.brand, product?.category])
      .filter((value): value is string => !!value)
      .slice(0, 200);
  }

  get categories(): string[] {
    return Array.from(new Set(this.allProducts.map((p) => `${p?.category || ''}`.trim()).filter(Boolean))).sort();
  }

  get brands(): string[] {
    return Array.from(new Set(this.allProducts.map((p) => `${p?.brand || ''}`.trim()).filter(Boolean))).sort();
  }

  canCreateProduct(): boolean {
    return this.authState.isAdmin() || this.authState.isManager() || this.authState.hasPermission(PERMISSIONS.PRODUCT_CREATE);
  }

  canEditProduct(): boolean {
    return this.authState.isAdmin() || this.authState.isManager() || this.authState.hasPermission(PERMISSIONS.PRODUCT_UPDATE);
  }

  canDeleteProduct(): boolean {
    return this.authState.isAdmin() || this.authState.isManager() || this.authState.hasPermission(PERMISSIONS.PRODUCT_DELETE);
  }

  isUserRole(): boolean {
    return this.authState.isUser();
  }

  isAdminContext(): boolean {
    return this.authState.isAdmin() || this.authState.isManager() || this.canCreateProduct() || this.canEditProduct() || this.canDeleteProduct();
  }

  loadProducts(): void {
    this.isLoading = true;
    this.productService.getAllProducts().subscribe({
      next: (response) => {
        this.allProducts = Array.isArray(response) ? response : [];
        this.applyFiltersAndPaging();
        this.isLoading = false;
        this.changeDetectorRef.markForCheck();
      },
      error: (error) => {
        console.error('Error loading products:', error);
        this.isLoading = false;
        this.showToast('danger', 'Error', 'Unable to load products right now.');
        this.changeDetectorRef.markForCheck();
      }
    });
  }

  onSearch(query: string): void {
    this.searchTerm = query;
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  onSearchChange(query: string): void {
    this.searchTerm = query;
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  onSuggestionSelected(value: string): void {
    this.searchTerm = value;
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  onSortChange(event: Event): void {
    const target = event.target as HTMLSelectElement;
    this.sortBy = (target.value as 'featured' | 'price-asc' | 'price-desc' | 'name-asc') || 'featured';
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  setCategory(category: string): void {
    this.selectedCategory = category;
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  setBrand(brand: string): void {
    this.selectedBrand = brand;
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedCategory = 'all';
    this.selectedBrand = 'all';
    this.sortBy = 'featured';
    this.currentPage = 0;
    this.applyFiltersAndPaging();
  }

  onPageChange(page: number): void {
    this.currentPage = page;
    this.applyPageSlice(this.filteredProducts);
  }

  onAddProduct(): void {
    if (!this.canCreateProduct()) {
      return;
    }
    this.router.navigate(['/products/create']);
  }

  onViewProduct(id: number): void {
    this.router.navigate(['/products', id]);
  }

  onEditProduct(id: number): void {
    if (!this.canEditProduct()) {
      return;
    }
    this.router.navigate(['/products/edit', id]);
  }

  confirmDeleteProduct(id: number): void {
    if (!this.canDeleteProduct()) {
      return;
    }
    this.deletingProductId = id;
    this.showDeleteDialog = true;
  }

  onDeleteConfirmed(): void {
    const id = this.deletingProductId;
    this.showDeleteDialog = false;
    this.deletingProductId = null;

    if (!id) {
      return;
    }

    this.productService.deleteProduct(id).subscribe({
      next: () => {
        this.showToast('success', 'Deleted', 'Product removed successfully.');
        this.loadProducts();
      },
      error: (error: any) => {
        console.error('Delete failed:', error);
        this.showToast('danger', 'Delete failed', error?.error?.message || 'Please try again.');
      }
    });
  }

  onDeleteCancelled(): void {
    this.showDeleteDialog = false;
    this.deletingProductId = null;
  }

  onAddedToCart(): void {
    this.showToast('success', 'Added to cart', 'Product added to your cart.');
  }

  onQuickView(product: any): void {
    this.onViewProduct(Number(product?.id));
  }

  getStockLabel(product: any): string {
    const stock = Number(product?.stock ?? 0);
    return stock <= 0 ? 'Out of stock' : `${stock} in stock`;
  }

  getStockClass(product: any): string {
    return Number(product?.stock ?? 0) > 0 ? 'in-stock' : 'out-of-stock';
  }

  getShortDescription(product: any): string {
    const description = `${product?.description ?? ''}`.trim();
    if (!description) {
      return 'Premium product for your daily ecommerce needs.';
    }
    return description.length > 90 ? `${description.slice(0, 90)}...` : description;
  }

  private applyFiltersAndPaging(): void {
    const query = this.searchTerm.trim().toLowerCase();

    let source = this.allProducts.filter((product) => {
      const matchesQuery = !query || `${product?.name || ''}`.toLowerCase().includes(query)
        || `${product?.category || ''}`.toLowerCase().includes(query)
        || `${product?.brand || ''}`.toLowerCase().includes(query);
      const matchesCategory = this.selectedCategory === 'all' || `${product?.category || ''}` === this.selectedCategory;
      const matchesBrand = this.selectedBrand === 'all' || `${product?.brand || ''}` === this.selectedBrand;
      return matchesQuery && matchesCategory && matchesBrand;
    });

    source = this.sortProducts(source);

    this.filteredProducts = source;
    this.totalPages = Math.max(1, Math.ceil(this.filteredProducts.length / this.pageSize));
    if (this.currentPage > this.totalPages - 1) {
      this.currentPage = 0;
    }
    this.applyPageSlice(this.filteredProducts);
  }

  private sortProducts(source: any[]): any[] {
    const copy = [...source];
    if (this.sortBy === 'price-asc') {
      return copy.sort((a, b) => Number(a?.cost || 0) - Number(b?.cost || 0));
    }
    if (this.sortBy === 'price-desc') {
      return copy.sort((a, b) => Number(b?.cost || 0) - Number(a?.cost || 0));
    }
    if (this.sortBy === 'name-asc') {
      return copy.sort((a, b) => `${a?.name || ''}`.localeCompare(`${b?.name || ''}`));
    }
    return copy;
  }

  private applyPageSlice(source: any[]): void {
    const start = this.currentPage * this.pageSize;
    const end = start + this.pageSize;
    this.products = source.slice(start, end);
  }

  private showToast(variant: 'success' | 'danger' | 'warning' | 'info', title: string, message: string): void {
    this.toastVariant = variant;
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastShow = true;
  }
}
