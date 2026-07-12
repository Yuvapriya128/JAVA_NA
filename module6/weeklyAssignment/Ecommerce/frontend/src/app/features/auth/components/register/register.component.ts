import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';
import { CustomerService } from '../../../../core/services/customer.service';
import { CustomerRequestDTO } from '../../../../core/dto/customer/customer.dto';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';
  readonly registerForm;

  constructor(
    private formBuilder: FormBuilder,
    private customerService: CustomerService,
    private router: Router
  ) {
    this.registerForm = this.formBuilder.group(
      {
        name: ['', [Validators.required, Validators.minLength(3)]],
        email: ['', [Validators.required, Validators.email]],
        address: ['', [Validators.required, Validators.minLength(5)]],
        password: ['', [Validators.required, Validators.minLength(6)]],
        confirmPassword: ['', [Validators.required]]
      },
      { validators: this.passwordMatchValidator }
    );
  }

  onRegister(): void {
    if (this.registerForm.invalid || this.isSubmitting) {
      this.registerForm.markAllAsTouched();
      this.errorMessage = 'Please fix the validation errors and try again.';
      return;
    }

    this.errorMessage = '';
    this.successMessage = '';
    this.isSubmitting = true;

    const formValue = this.registerForm.getRawValue();
    const payload: CustomerRequestDTO = {
      name: (formValue.name ?? '').trim(),
      email: (formValue.email ?? '').trim(),
      address: (formValue.address ?? '').trim(),
      password: formValue.password ?? ''
    };

    this.customerService.register(payload)
      .pipe(finalize(() => {
        this.isSubmitting = false;
      }))
      .subscribe({
        next: () => {
          this.successMessage = 'Registration Successful';
          this.registerForm.reset();
          setTimeout(() => {
            this.router.navigate(['/auth/login']);
          }, 2000);
        },
        error: (error: HttpErrorResponse) => {
          if (error.status === 400) {
            this.errorMessage = 'Validation failed. Please check your details.';
            return;
          }

          if (error.status === 409) {
            this.errorMessage = 'Email already exists.';
            return;
          }

          if (error.status === 500) {
            this.errorMessage = 'Something went wrong.';
            return;
          }

          this.errorMessage = 'Something went wrong.';
        }
      });
  }

  get nameControl(): AbstractControl | null {
    return this.registerForm.get('name');
  }

  get emailControl(): AbstractControl | null {
    return this.registerForm.get('email');
  }

  get addressControl(): AbstractControl | null {
    return this.registerForm.get('address');
  }

  get passwordControl(): AbstractControl | null {
    return this.registerForm.get('password');
  }

  get confirmPasswordControl(): AbstractControl | null {
    return this.registerForm.get('confirmPassword');
  }

  private passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (!password || !confirmPassword) {
      return null;
    }

    return password === confirmPassword ? null : { passwordMismatch: true };
  }
}

