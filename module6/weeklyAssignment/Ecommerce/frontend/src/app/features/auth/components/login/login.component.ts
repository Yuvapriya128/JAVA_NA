import { Component, OnInit, OnDestroy, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { trigger, transition, style, animate } from '@angular/animations';
import { AuthService } from '../../../../core/auth/auth.service';
import { AuthStateService } from '../../../../core/auth/auth-state.service';
import { AuthRequestDTO } from '../../../../core/auth/dto/auth.dto';
import { ROLES } from '../../../../core/auth/constants/roles.constants';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
  animations: [
    trigger('slideIn', [
      transition(':enter', [
        style({ opacity: 0, transform: 'translateY(-10px)' }),
        animate('300ms ease-out', style({ opacity: 1, transform: 'translateY(0)' }))
      ]),
      transition(':leave', [
        animate('300ms ease-in', style({ opacity: 0, transform: 'translateY(-10px)' }))
      ])
    ])
  ]
})
export class LoginComponent implements OnInit, OnDestroy {
  loginForm: FormGroup;
  showPassword = false;
  private authStateService = inject(AuthStateService);

  constructor(
    private formBuilder: FormBuilder,
    public authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      rememberMe: [false]
    });

    effect(() => {
      const isLoading = (this.authService.authStatus)() === 'loading';
      const emailControl = this.loginForm.get('email');
      const passwordControl = this.loginForm.get('password');

      if (isLoading) {
        emailControl?.disable({ emitEvent: false });
        passwordControl?.disable({ emitEvent: false });
      } else {
        emailControl?.enable({ emitEvent: false });
        passwordControl?.enable({ emitEvent: false });
      }
    });
  }

  ngOnInit(): void {
    // Reset auth status on component init
    this.authService.resetAuthStatus();
  }

  ngOnDestroy(): void {
    // Reset auth status when leaving login page
    this.authService.resetAuthStatus();
  }

  onSubmit(): void {
    // Prevent double-submit
    if ((this.authService.authStatus)() === 'loading') {
      return;
    }

    // Validate form
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    // Submit login
    const credentials: AuthRequestDTO = this.loginForm.value;
    this.authService.login(credentials).subscribe({
      next: () => {
        console.log('Login Success');

        // Get user role and navigate accordingly
        const userRole = this.authStateService.currentUser()?.role;
        let navigatePath = '/products'; // Default for users

        if (userRole === ROLES.ADMIN || userRole === ROLES.MANAGER) {
          navigatePath = '/dashboard'; // Admin/Manager goes to dashboard
        }

        console.log(`User role: ${userRole}, navigating to: ${navigatePath}`);
        this.router.navigate([navigatePath]).then((navigated) => {
          console.log(`Navigation to ${navigatePath} result:`, navigated);
        }).catch((error) => {
          console.error(`Navigation to ${navigatePath} error:`, error);
        });
      },
      error: () => {
        // Error handled in service - display alert via signal
        console.error('Login failed');
      }
    });
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  isFieldInvalid(fieldName: string): boolean {
    const control = this.loginForm.get(fieldName);
    const hasFieldError = (this.authService.fieldErrors)()[fieldName];
    return !!(hasFieldError || (control?.invalid && control?.touched));
  }

  getFieldError(fieldName: string): string {
    const fieldErrors = (this.authService.fieldErrors)();
    if (fieldErrors[fieldName]) {
      return fieldErrors[fieldName];
    }

    const control = this.loginForm.get(fieldName);
    if (control?.hasError('required')) {
      return `${fieldName.charAt(0).toUpperCase() + fieldName.slice(1)} is required`;
    }
    if (control?.hasError('email')) {
      return 'Please enter a valid email address';
    }
    if (control?.hasError('minlength')) {
      return `${fieldName} must be at least 6 characters`;
    }
    return '';
  }
}


