import { AbstractControl, ValidationErrors, ValidatorFn } from '@angular/forms';
import { Component, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService, RegisterRequest } from '../../../services/auth/auth.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

const passwordMatchValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
  const password = control.get('password')?.value;
  const confirmPassword = control.get('confirmPassword')?.value;
  if (!password || !confirmPassword) {
    return null;
  }
  return password === confirmPassword ? null : { passwordMismatch: true };
};

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  readonly registerForm = this.fb.group({
    customerName: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50), Validators.pattern(/^[A-Za-z ]+$/)]],
    email: ['', [Validators.required, Validators.email]],
    phoneNumber: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
    city: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
    password: ['', [Validators.required, Validators.minLength(8), Validators.pattern(/^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).+$/)]],
    confirmPassword: ['', [Validators.required]],
    acceptTerms: [false, [Validators.requiredTrue]]
  }, { validators: [passwordMatchValidator] });

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');

  submit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Please fix validation errors before continuing.' });
      this.successMessage.set('');
      return;
    }

    const form = this.registerForm.getRawValue();
    const registerDto: RegisterRequest = {
      customerName: form.customerName || '',
      email: form.email || '',
      phoneNumber: form.phoneNumber || '',
      city: form.city || '',
      password: form.password || ''
    };

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');

    this.authService.register(registerDto).subscribe({
      next: () => {
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Registration submitted. Wait for admin approval before login.');
        this.router.navigate(['/login'], {
          queryParams: {
            registered: '1',
            email: registerDto.email
          }
        });
      },
      error: (error) => {
        const serverMessage = String(error?.error?.message || error?.message || '');
        const normalized = serverMessage.toLowerCase();
        let errorMessage = 'Unable to complete registration.';

        if (serverMessage.includes('An account with this email already exists')) {
          errorMessage = 'An account with this email already exists';
        } else if (serverMessage.includes('An account with this phone number already exists')) {
          errorMessage = 'An account with this phone number already exists';
        } else if (normalized.includes('duplicate or invalid data')) {
          errorMessage = 'Registration failed due to server data conflict. Please try again.';
        } else if (serverMessage) {
          errorMessage = serverMessage;
        }

        this.status.set({
          loading: false,
          success: false,
          error: errorMessage
        });
        this.successMessage.set('');
      }
    });
  }

  controlInvalid(name: keyof typeof this.registerForm.controls): boolean {
    const control = this.registerForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  hasMinLength(): boolean {
    return (this.registerForm.controls.password.value || '').length >= 8;
  }

  hasUppercase(): boolean {
    return /[A-Z]/.test(this.registerForm.controls.password.value || '');
  }

  hasLowercase(): boolean {
    return /[a-z]/.test(this.registerForm.controls.password.value || '');
  }

  hasDigit(): boolean {
    return /\d/.test(this.registerForm.controls.password.value || '');
  }

  hasSpecialCharacter(): boolean {
    return /[^A-Za-z0-9]/.test(this.registerForm.controls.password.value || '');
  }

  showPasswordValidation(): boolean {
    const control = this.registerForm.controls.password;
    return control.touched || !!control.value;
  }

  hasConfirmPasswordMismatch(): boolean {
    const confirmControl = this.registerForm.controls.confirmPassword;
    return !!(this.registerForm.errors?.['passwordMismatch'] && (confirmControl.touched || confirmControl.dirty));
  }
}
