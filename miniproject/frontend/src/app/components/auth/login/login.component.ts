import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AuthService, LoginRequest } from '../../../services/auth/auth.service';
import { TokenStorageService } from '../../../services/auth/token-storage.service';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']})
export class LoginComponent implements OnInit {
  private readonly authService = inject(AuthService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly fb = inject(FormBuilder);

  readonly loginForm = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]]
  });

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');

  ngOnInit(): void {
    const registered = this.route.snapshot.queryParamMap.get('registered') === '1';
    const email = this.route.snapshot.queryParamMap.get('email') || '';
    if (registered) {
      this.status.set({
        loading: false,
        success: true,
        error: ''
      });
      this.successMessage.set('Registration submitted. Wait for admin approval before login.');
      if (email) {
        this.loginForm.patchValue({ email });
      }
    }

    if (this.tokenStorage.isLoggedIn()) {
      this.router.navigateByUrl(this.getDefaultRouteForRole());
    }
  }

  submit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      this.status.set({ loading: false, success: false, error: 'Please fix validation errors before submitting.' });
      this.successMessage.set('');
      return;
    }

    this.status.set({ loading: true, success: false, error: '' });
    this.successMessage.set('');
    const loginDto: LoginRequest = {
      email: this.loginForm.controls.email.value || '',
      password: this.loginForm.controls.password.value || ''
    };

    this.authService.login(loginDto).subscribe({
      next: (response) => {
        this.tokenStorage.saveToken(response.token);
        const defaultRoute = this.getDefaultRouteForRole();
        this.status.set({
          loading: false,
          success: true,
          error: ''
        });
        this.successMessage.set('Login successful. Redirecting...');
        this.reset();
        const returnUrl = this.route.snapshot.queryParamMap.get('returnUrl') || defaultRoute;
        this.router.navigateByUrl(returnUrl);
      },
      error: (error) => {
        const message = String(error?.error?.message || error?.message || '');
        const normalizedMessage = message.toLowerCase();
        const isInactiveAccount =
          normalizedMessage.includes('account is inactive. await admin approval.') ||
          normalizedMessage.includes('inactive') || normalizedMessage.includes('await admin approval');
        const isInvalidCredentialError =
          error?.status === 401 ||
          normalizedMessage.includes('invalid credential') ||
          normalizedMessage.includes('invalid email') ||
          normalizedMessage.includes('invalid password');

        this.status.set({
          loading: false,
          success: false,
          error: isInactiveAccount
            ? 'Your account is pending admin approval.'
            : (isInvalidCredentialError
              ? 'Invalid credentials. Please verify your email and password.'
              : error?.error?.message || error?.message || 'Something went wrong.')
        });
        this.successMessage.set('');
      }
    });
  }

  reset(): void {
    this.loginForm.reset({
      email: '',
      password: ''
    });
  }

  controlInvalid(name: 'email' | 'password'): boolean {
    const control = this.loginForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  private getDefaultRouteForRole(): string {
    return '/dashboard';
  }
}
