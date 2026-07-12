import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { ProductRequestDTO } from '../../../../core/dto/product/product.dto';
import { ProductService } from '../../../../core/services/product.service';

@Component({
  selector: 'app-product-create',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './product-create.component.html',
  styleUrl: './product-create.component.css'})
export class ProductCreateComponent {
  form: FormGroup;
  isSubmitting = false;
  error = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private productService: ProductService
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      cost: ['', [Validators.required, Validators.min(0)]],
      description: [''],
      stock: ['', [Validators.required, Validators.min(0)]],
      minimumStock: ['', [Validators.min(0)]],
      category: ['', Validators.required],
      brand: [''],
      active: [true]
    });
  }

  onSubmit(): void {
    console.log('[ProductCreateComponent] Submit clicked');
    console.log('[ProductCreateComponent] Form valid:', this.form.valid);
    console.log('[ProductCreateComponent] isSubmitting:', this.isSubmitting);
    if (this.isSubmitting) {
      return;
    }

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      this.error = 'Please fix the validation errors before submitting.';
      return;
    }

    this.isSubmitting = true;
    this.error = '';
     const payload: ProductRequestDTO = {
       name: (this.form.get('name')?.value ?? '').trim(),
       brand: (this.form.get('brand')?.value ?? '').trim(),
       category: (this.form.get('category')?.value ?? '').trim(),
       cost: Number(this.form.get('cost')?.value),
       description: (this.form.get('description')?.value ?? '').trim() || undefined,
       stock: Number(this.form.get('stock')?.value),
       minimumStock: Number(this.form.get('minimumStock')?.value) || undefined,
       active: this.form.get('active')?.value ?? true
     };
    console.log('[ProductCreateComponent] Payload:', payload);

    this.productService.createProduct(payload)
      .pipe(finalize(() => {
        this.isSubmitting = false;
      }))
      .subscribe({
      next: (response) => {
        console.log('[ProductCreateComponent] API Response:', response);
        this.router.navigate(['/products']);
      },
      error: (error) => {
        console.error('[ProductCreateComponent] API Error:', error);
        this.error = this.getBackendErrorMessage(error, 'product');
      }
    });
  }

  onCancel(): void {
    this.router.navigate(['/products']);
  }

  private getBackendErrorMessage(error: any, entity: string): string {
    const details = error?.error?.message || error?.error?.error || '';

    switch (error?.status) {
      case 400:
        return details || `Invalid ${entity} data. Please review all fields.`;
      case 401:
        return 'Your session has expired. Please log in again.';
      case 403:
        return `You do not have permission to create ${entity}s.`;
      case 404:
        return 'Required API endpoint was not found.';
      case 409:
        return details || `A ${entity} with the same information already exists.`;
      case 500:
        return 'Server error occurred while creating the record. Please try again.';
      default:
        return details || `Error creating ${entity}.`;
    }
  }
}

