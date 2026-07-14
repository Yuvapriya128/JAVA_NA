import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { CustomerService } from '../../../../core/services/customer.service';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';

@Component({
  selector: 'app-customer-edit',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './customer-edit.component.html',
  styleUrl: './customer-edit.component.css'})
export class CustomerEditComponent implements OnInit, OnDestroy {
  form: FormGroup;
  isLoading = true;
  isSubmitting = false;
  error = '';
  customerId: number = 0;
  private destroy$ = new Subject<void>();

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private customerService: CustomerService,
    private cdr: ChangeDetectorRef
  ) {
    this.form = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(3)]],
      email: ['', [Validators.required, Validators.email]],
      address: ['', [Validators.required, Validators.minLength(5)]],
      phoneNumber: ['', [Validators.pattern(/^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/)]]
    });
  }

  ngOnInit(): void {
    console.log('[CustomerEditComponent] Edit Component Loaded');
    const id = this.route.snapshot.paramMap.get('id');
    console.log('[CustomerEditComponent] Extracted ID from snapshot:', id);

    if (!id || isNaN(+id) || +id <= 0) {
      console.warn('[CustomerEditComponent] Invalid ID:', id);
      this.isLoading = false;
      this.error = 'Invalid customer ID';
      this.cdr.detectChanges();
      return;
    }

    this.customerId = +id;
    this.loadCustomer();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadCustomer(): void {
    // Validate route parameter
    if (!this.customerId || isNaN(this.customerId) || this.customerId <= 0) {
      console.warn('[CustomerEditComponent] Invalid ID:', this.customerId);
      this.isLoading = false;
      this.error = 'Invalid customer ID';
      this.cdr.detectChanges();
      return;
    }

    console.log('[CustomerEditComponent] Calling getById:', this.customerId);
    this.customerService.getCustomerById(this.customerId)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isLoading = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (data) => {
          console.log('[CustomerEditComponent] API Response:', data);
          this.form.patchValue({
            name: data.name,
            email: data.email,
            address: data.address,
            phoneNumber: data.phoneNumber || ''
          });
          this.error = '';
          console.log('[CustomerEditComponent] Form patched successfully');
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('[CustomerEditComponent] API Error:', error);
          if (error.status === 404) {
            this.error = 'Customer not found';
          } else if (error.status === 403) {
            this.error = 'Access denied';
          } else {
            this.error = error?.error?.message || 'Error loading customer';
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

    console.log('Submitting', this.form.value);

    this.customerService.updateCustomer(this.customerId, this.form.value)
      .pipe(
        takeUntil(this.destroy$),
        finalize(() => {
          this.isSubmitting = false;
          this.cdr.detectChanges();
        })
      )
      .subscribe({
        next: (response) => {
          console.log('Response', response);
          this.router.navigate(['/customers']);
        },
        error: (error) => {
          this.error = error?.error?.message || 'Error updating customer';
          this.cdr.detectChanges();
        }
      });
  }

  onCancel(): void {
    this.router.navigate(['/customers']);
  }
}



