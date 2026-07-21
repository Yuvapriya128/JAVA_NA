import { Component, OnDestroy, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { AuthService } from '../../services/auth/auth.service';
import { CustomerResponse } from '../../services/customer/customer.service';
import { PageHeaderComponent } from '../shared/page-header/page-header.component';
import { UiStatusState, defaultUiStatus } from '../../constants/ui-status';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [ReactiveFormsModule, PageHeaderComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css'],
})
export class ProfileComponent implements OnInit, OnDestroy {
  private readonly authService = inject(AuthService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private redirectTimer: ReturnType<typeof setTimeout> | null = null;

  readonly profile = signal<CustomerResponse | null>(null);
  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly section = signal<'profile' | 'password'>('profile');
  readonly passwordState = signal({ loading: false, error: '', success: '' });

  readonly passwordForm = this.fb.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required]]
  });

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const rawSection = params.get('section');
      if (rawSection === 'password') {
        this.section.set('password');
        return;
      }
      this.section.set('profile');
    });

    this.loadData();
  }

  ngOnDestroy(): void {
    if (this.redirectTimer) {
      clearTimeout(this.redirectTimer);
      this.redirectTimer = null;
    }
  }

  sectionTitle(): string {
    if (this.section() === 'password') {
      return 'Change Password';
    }
    return 'My Details';
  }

  sectionDescription(): string {
    if (this.section() === 'password') {
      return 'Password update flow is managed through secured authentication services.';
    }
    return 'View your profile information.';
  }

  submitPasswordChange(): void {
    this.passwordState.set({ loading: false, error: '', success: '' });

    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      this.passwordState.set({ loading: false, error: 'Please fill all password fields correctly.', success: '' });
      return;
    }

    const currentPassword = this.passwordForm.controls.currentPassword.value?.trim() || '';
    const newPassword = this.passwordForm.controls.newPassword.value?.trim() || '';
    const confirmPassword = this.passwordForm.controls.confirmPassword.value?.trim() || '';

    if (newPassword !== confirmPassword) {
      this.passwordState.set({ loading: false, error: 'New password and confirm password must match.', success: '' });
      return;
    }

    if (newPassword.length < 8) {
      this.passwordState.set({ loading: false, error: 'New password must be at least 8 characters.', success: '' });
      return;
    }

    this.passwordState.set({ loading: true, error: '', success: '' });
    this.authService.changePassword({ currentPassword, newPassword }).subscribe({
      next: (response) => {
        this.passwordState.set({
          loading: false,
          error: '',
          success: response?.message || 'Password changed successfully.'
        });
        this.passwordForm.reset({
          currentPassword: '',
          newPassword: '',
          confirmPassword: ''
        });

        if (this.redirectTimer) {
          clearTimeout(this.redirectTimer);
        }
        this.redirectTimer = setTimeout(() => {
          this.router.navigate([], {
            relativeTo: this.route,
            queryParams: { section: 'profile' },
            queryParamsHandling: 'merge'
          });
        }, 1500);
      },
      error: (error) => {
        this.passwordState.set({
          loading: false,
          error: error?.error?.message || error?.message || 'Failed to change password.',
          success: ''
        });
      }
    });
  }

  loadData(): void {
    this.status.set({ loading: true, success: false, error: '' });
    this.authService.getCurrentUser().subscribe({
      next: (response) => {
        this.profile.set(response);
        this.status.set({ loading: false, success: false, error: '' });
      },
      error: (error) => {
        this.status.set({
          loading: false,
          success: false,
          error: error?.error?.message || error?.message || 'Something went wrong.'
        });
      }
    });
  }

  passwordControlInvalid(name: keyof typeof this.passwordForm.controls): boolean {
    const control = this.passwordForm.controls[name];
    return control.invalid && (control.dirty || control.touched);
  }

  openSection(section: 'profile' | 'password'): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: { section },
      queryParamsHandling: 'merge'
    });
  }
}
