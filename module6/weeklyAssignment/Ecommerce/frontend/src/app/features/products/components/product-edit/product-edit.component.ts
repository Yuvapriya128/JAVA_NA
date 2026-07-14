import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ProductService } from '../../../../core/services/product.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-product-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-edit.component.html',
  styleUrl: './product-edit.component.css'})
export class ProductEditComponent implements OnInit, OnDestroy {
  form: FormGroup;
  isLoading = true;
  isSubmitting = false;
  error = '';
  productId: number = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private productService: ProductService,
    private cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      cost: ['', [Validators.required, Validators.min(0)]],
      description: [''],
      stock: ['', [Validators.required, Validators.min(0)]],
      category: ['', Validators.required],
      brand: ['']
    });
  }

  ngOnInit(): void {
    console.log('[ProductEditComponent] Edit Component Loaded');
    const id = this.route.snapshot.paramMap.get('id');
    console.log('[ProductEditComponent] Extracted ID from snapshot:', id);

    if (!id || isNaN(+id) || +id <= 0) {
      console.warn('[ProductEditComponent] Invalid ID:', id);
      this.isLoading = false;
      this.error = 'Invalid product ID';
      this.cdr.detectChanges();
      return;
    }

    this.productId = +id;
    this.loadProduct();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadProduct(): void {
    // Validate route parameter
    if (!this.productId || isNaN(this.productId) || this.productId <= 0) {
      console.warn('[ProductEditComponent] Invalid ID:', this.productId);
      this.isLoading = false;
      this.error = 'Invalid product ID';
      this.cdr.detectChanges();
      return;
    }

    console.log('[ProductEditComponent] Calling getById:', this.productId);
    this.productService.getProductById(this.productId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          console.log('[ProductEditComponent] API Response:', data);
          this.form.patchValue({
            name: data.name,
            cost: data.cost,
            description: data.description,
            stock: data.stock,
            category: data.category,
            brand: data.brand
          });
          this.error = '';
          console.log('[ProductEditComponent] Form patched successfully');
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[ProductEditComponent] API Error:', error);
          if (error.status === 404) {
            this.error = 'Product not found';
          } else if (error.status === 403) {
            this.error = 'Access denied';
          } else {
            this.error = error?.error?.message || 'Error loading product';
          }
          this.cdr.detectChanges();
        }
      });
  }

  onSubmit(): void {
    if (this.isSubmitting) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.isSubmitting = true;
    this.error = '';

    this.productService.updateProduct(this.productId, this.form.value)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: () => {
          this.router.navigate(['/products']);
        },
        error: (error) => {
          this.error = error?.error?.message || 'Error updating product';
          this.cdr.detectChanges();
        }
      });
  }

  onCancel(): void {
    this.router.navigate(['/products']);
  }
}

