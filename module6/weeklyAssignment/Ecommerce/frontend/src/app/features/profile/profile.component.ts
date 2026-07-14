import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AbstractControl, FormBuilder, FormGroup, ReactiveFormsModule, ValidationErrors, Validators } from '@angular/forms';
import { finalize } from 'rxjs/operators';
import { AuthStateService } from '../../core/auth/auth-state.service';
import { CustomerMeResponseDTO, CustomerMeUpdateDTO, CustomerPasswordChangeDTO } from '../../core/dto/customer/customer.dto';
import { CustomerService } from '../../core/services/customer.service';
import { CustomValidators } from '../../shared/validators/custom-validators';
import { ToastComponent } from '../../shared/components/toast/toast.component';
import { LoadingComponent } from '../../shared/components/loading/loading.component';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, ToastComponent, LoadingComponent],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'})

export class ProfileComponent implements OnInit {
  private readonly authState = inject(AuthStateService);
  private readonly customerService = inject(CustomerService);
  private readonly fb = inject(FormBuilder);

  readonly canEditEmail = false;
  private readonly phonePattern = /^[\+]?[(]?[0-9]{3}[)]?[-\s\.]?[0-9]{3}[-\s\.]?[0-9]{4,6}$/;
  readonly profileForm: FormGroup = this.fb.group({
    firstName: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
    lastName: ['', [Validators.maxLength(50)]],
    phoneNumber: ['', [Validators.required, Validators.pattern(this.phonePattern)]],
    address: ['', [Validators.required, Validators.minLength(5), Validators.maxLength(255)]],
    email: [{ value: '', disabled: !this.canEditEmail }, [Validators.required, Validators.email, Validators.maxLength(100)]],
    role: [{ value: '', disabled: true }],
    customerId: [{ value: '', disabled: true }]
  });

  readonly passwordForm: FormGroup = this.fb.group(
    {
      currentPassword: ['', [Validators.required]],
      newPassword: ['', [Validators.required, Validators.minLength(8), CustomValidators.passwordStrength()]],
      confirmPassword: ['', [Validators.required]]
    },
    { validators: this.passwordsMatchValidator }
  );

  readonly isProfileLoading = signal(true);
  readonly profileLoadError = signal('');
  readonly isProfileSaving = signal(false);
  readonly isPasswordSaving = signal(false);
  readonly isEditMode = signal(false);
  readonly isPasswordMode = signal(false);

  toastShow = false;
  toastTitle = '';
  toastMessage = '';
  toastVariant: 'success' | 'danger' | 'warning' | 'info' = 'info';

  readonly displayName = computed(() => {
    const firstName = this.safeString(this.profileForm.get('firstName')?.value);
    const lastName = this.safeString(this.profileForm.get('lastName')?.value);
    const fullName = `${firstName} ${lastName}`.trim();
    return fullName || this.authState.currentUser()?.name || 'User';
  });

  readonly avatarInitials = computed(() => {
    const first = this.safeString(this.profileForm.get('firstName')?.value);
    const last = this.safeString(this.profileForm.get('lastName')?.value);
    const initials = `${first.charAt(0)}${last.charAt(0)}`.trim().toUpperCase();
    return initials || 'U';
  });

  ngOnInit(): void {
    this.loadProfile();
  }

  openEditMode(): void {
    this.isEditMode.set(true);
    this.isPasswordMode.set(false);
  }

  cancelEditMode(): void {
    this.isEditMode.set(false);
    this.loadProfile();
  }

  openPasswordMode(): void {
    this.isPasswordMode.set(true);
    this.isEditMode.set(false);
    this.passwordForm.reset();
    this.clearServerErrors(this.passwordForm);
  }

  cancelPasswordMode(): void {
    this.isPasswordMode.set(false);
    this.passwordForm.reset();
    this.clearServerErrors(this.passwordForm);
  }

  loadProfile(): void {
    console.log('[ProfileComponent] loadProfile:start');
    console.log('[ProfileComponent] loading -> true');
    this.isProfileLoading.set(true);
    this.profileLoadError.set('');

    this.customerService
      .getMyProfile()
      .subscribe({
        next: (response) => {
          console.log('[ProfileComponent] getMyProfile:next raw response', response);

          try {
            const mappedProfile = this.normalizeProfileResponse(response);
            console.log('[ProfileComponent] getMyProfile:mapped profile', mappedProfile);

            this.applyProfile(mappedProfile);
            this.profileLoadError.set('');
            console.log('[ProfileComponent] profileForm patched', this.profileForm.getRawValue());
          } catch (mappingError) {
            console.error('[ProfileComponent] profile mapping/patch error', mappingError);
            this.profileLoadError.set('Unable to render profile data. Please reload.');
          } finally {
            this.isProfileLoading.set(false);
            console.log('[ProfileComponent] loading -> false (next/finally)');
          }
        },
        error: (error) => {
          console.error('[ProfileComponent] getMyProfile:error', error);
          this.profileLoadError.set(this.resolveErrorMessage(error?.status, error?.error?.message));
          this.isProfileLoading.set(false);
          console.log('[ProfileComponent] loading -> false (error)');
          this.showToast('danger', 'Profile load failed', this.resolveErrorMessage(error?.status, error?.error?.message));
        },
        complete: () => {
          console.log('[ProfileComponent] getMyProfile:complete');
        }
      });
  }

  onSaveProfile(): void {
    if (this.isProfileSaving()) {
      return;
    }

    this.clearServerErrors(this.profileForm);
    if (this.profileForm.invalid) {
      this.profileForm.markAllAsTouched();
      return;
    }

    const raw = this.profileForm.getRawValue();
    const firstName = this.safeString(raw.firstName);
    const lastName = this.safeString(raw.lastName);
    const fullName = `${firstName} ${lastName}`.trim();
    const phoneNumber = this.safeString(raw.phoneNumber);
    const email = this.safeString(raw.email);
    const payload: CustomerMeUpdateDTO = {
      name: fullName || firstName,
      firstName,
      lastName,
      phoneNumber,
      phone: phoneNumber,
      address: this.safeString(raw.address),
      email
    };

    this.isProfileSaving.set(true);
    this.customerService
      .updateMyProfile(payload)
      .pipe(finalize(() => this.isProfileSaving.set(false)))
      .subscribe({
        next: () => {
          this.showToast('success', 'Profile updated', 'Your profile was updated successfully.');
          this.isEditMode.set(false);
          this.loadProfile();
        },
        error: (error) => {
          this.applyServerErrors(this.profileForm, error?.error);
          this.showToast('danger', 'Update failed', this.resolveErrorMessage(error?.status, error?.error?.message));
        }
      });
  }

  onChangePassword(): void {
    if (this.isPasswordSaving()) {
      return;
    }

    this.clearServerErrors(this.passwordForm);
    if (this.passwordForm.invalid) {
      this.passwordForm.markAllAsTouched();
      return;
    }

    const raw = this.passwordForm.getRawValue();
    const payload: CustomerPasswordChangeDTO = {
      currentPassword: this.safeString(raw.currentPassword),
      newPassword: this.safeString(raw.newPassword),
      confirmPassword: this.safeString(raw.confirmPassword)
    };

    this.isPasswordSaving.set(true);
    this.customerService
      .changeMyPassword(payload)
      .pipe(finalize(() => this.isPasswordSaving.set(false)))
      .subscribe({
        next: () => {
          this.passwordForm.reset();
          this.isPasswordMode.set(false);
          this.showToast('success', 'Password updated', 'Your password was changed successfully.');
        },
        error: (error) => {
          this.applyServerErrors(this.passwordForm, error?.error);
          this.showToast('danger', 'Password change failed', this.resolveErrorMessage(error?.status, error?.error?.message));
        }
      });
  }

  getControlError(form: FormGroup, controlName: string): string {
    const control = form.get(controlName);
    if (!control || (!control.touched && !control.dirty)) {
      return '';
    }

    if (control.errors?.['server']) {
      return String(control.errors['server']);
    }
    if (control.errors?.['required']) {
      return 'This field is required.';
    }
    if (control.errors?.['email']) {
      return 'Enter a valid email address.';
    }
    if (control.errors?.['minlength']) {
      return `Minimum length is ${control.errors['minlength'].requiredLength}.`;
    }
    if (control.errors?.['maxlength']) {
      return `Maximum length is ${control.errors['maxlength'].requiredLength}.`;
    }
    if (control.errors?.['pattern']) {
      if (controlName === 'phoneNumber') {
        return 'Enter a valid phone number.';
      }
      return 'Invalid format.';
    }
    if (control.errors?.['weakPassword']) {
      return 'Password must include uppercase, lowercase, number, and special character.';
    }

    return '';
  }

  getPasswordConfirmError(): string {
    const confirmControl = this.passwordForm.get('confirmPassword');
    if (!confirmControl || (!confirmControl.touched && !confirmControl.dirty)) {
      return '';
    }

    if (confirmControl.errors?.['required']) {
      return 'Confirm password is required.';
    }

    if (this.passwordForm.errors?.['passwordMismatch']) {
      return 'Passwords do not match.';
    }

    return this.getControlError(this.passwordForm, 'confirmPassword');
  }

  private applyProfile(profile: CustomerMeResponseDTO): void {
    const customerId = this.resolveCustomerId(profile);
    const firstName = this.resolveFirstName(profile);
    const lastName = this.resolveLastName(profile);
    const email = this.safeString(profile.email || this.authState.currentUser()?.email || '');
    const role = this.safeString(profile.role || this.authState.currentUser()?.role || '');
    const phoneNumber = this.resolvePhone(profile);
    const address = this.safeString(profile.address || '');

    this.profileForm.patchValue({
      firstName,
      lastName,
      phoneNumber,
      address,
      email,
      role,
      customerId: customerId ? String(customerId) : ''
    });

    this.syncAuthState(customerId, firstName, lastName, email, role);
  }

  private normalizeProfileResponse(response: unknown): CustomerMeResponseDTO {
    const responseObj = this.asObject(response);
    const payload = this.extractPayload(responseObj);

    const name = this.firstNonEmpty(payload, ['name', 'customerName', 'fullName']);
    const email = this.firstNonEmpty(payload, ['email', 'customerEmail', 'userEmail']);
    const address = this.firstNonEmpty(payload, ['address', 'customerAddress']);
    const role = this.firstNonEmpty(payload, ['role', 'userRole']);
    const firstName = this.firstNonEmpty(payload, ['firstName', 'firstname', 'first_name']);
    const lastName = this.firstNonEmpty(payload, ['lastName', 'lastname', 'last_name']);
    const phoneNumber = this.firstNonEmpty(payload, ['phoneNumber', 'phone', 'phoneNo', 'mobile', 'mobileNumber']);

    const customerIdRaw = this.firstNonNull(payload, ['customerId', 'customerID', 'id', 'userId']);
    const customerId = this.toPositiveNumber(customerIdRaw);

    return {
      customerId: customerId ?? undefined,
      id: customerId ?? undefined,
      firstName,
      lastName,
      name,
      email,
      address,
      role,
      phoneNumber,
      phone: phoneNumber
    };
  }

  private extractPayload(responseObj: Record<string, unknown>): Record<string, unknown> {
    const nestedKeys = ['data', 'result', 'customer', 'profile', 'user'];
    for (const key of nestedKeys) {
      const nested = this.asObject(responseObj[key]);
      if (Object.keys(nested).length > 0) {
        return nested;
      }
    }

    return responseObj;
  }

  private firstNonEmpty(source: Record<string, unknown>, keys: string[]): string {
    for (const key of keys) {
      const value = this.safeString(source[key]);
      if (value) {
        return value;
      }
    }
    return '';
  }

  private firstNonNull(source: Record<string, unknown>, keys: string[]): unknown {
    for (const key of keys) {
      const value = source[key];
      if (value !== null && value !== undefined) {
        return value;
      }
    }
    return null;
  }

  private toPositiveNumber(value: unknown): number | null {
    const parsed = Number(value);
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
  }

  private asObject(value: unknown): Record<string, unknown> {
    if (value && typeof value === 'object' && !Array.isArray(value)) {
      return value as Record<string, unknown>;
    }
    return {};
  }

  private syncAuthState(customerId: number | null, firstName: string, lastName: string, email: string, role: string): void {
    const existingUser = this.authState.currentUser();
    if (!existingUser) {
      return;
    }

    this.authState.setCurrentUser({
      ...existingUser,
      customerId: customerId ?? existingUser.customerId,
      email: email || existingUser.email,
      role: role || existingUser.role,
      name: `${firstName} ${lastName}`.trim() || existingUser.name
    });
  }

  private resolveCustomerId(profile: CustomerMeResponseDTO): number | null {
    const fromCustomerId = Number(profile.customerId);
    if (Number.isFinite(fromCustomerId) && fromCustomerId > 0) {
      return fromCustomerId;
    }

    const fromId = Number(profile.id);
    if (Number.isFinite(fromId) && fromId > 0) {
      return fromId;
    }

    const authCustomerId = Number(this.authState.currentUser()?.customerId);
    return Number.isFinite(authCustomerId) && authCustomerId > 0 ? authCustomerId : null;
  }

  private resolveFirstName(profile: CustomerMeResponseDTO): string {
    const firstName = this.safeString(profile.firstName);
    if (firstName) {
      return firstName;
    }

    const parts = this.safeString(profile.name).split(' ').filter(Boolean);
    if (parts.length > 0) {
      return parts[0];
    }

    const tokenName = this.safeString(this.authState.currentUser()?.name);
    return tokenName.split(' ').filter(Boolean)[0] || '';
  }

  private resolveLastName(profile: CustomerMeResponseDTO): string {
    const lastName = this.safeString(profile.lastName);
    if (lastName) {
      return lastName;
    }

    const parts = this.safeString(profile.name).split(' ').filter(Boolean);
    if (parts.length > 1) {
      return parts.slice(1).join(' ');
    }

    const tokenParts = this.safeString(this.authState.currentUser()?.name).split(' ').filter(Boolean);
    if (tokenParts.length > 1) {
      return tokenParts.slice(1).join(' ');
    }

    return '';
  }

  private resolvePhone(profile: CustomerMeResponseDTO): string {
    const fromPhoneNumber = this.safeString(profile.phoneNumber);
    if (fromPhoneNumber) {
      return fromPhoneNumber;
    }

    return this.safeString(profile.phone);
  }

  private passwordsMatchValidator(control: AbstractControl): ValidationErrors | null {
    const newPassword = control.get('newPassword')?.value;
    const confirmPassword = control.get('confirmPassword')?.value;

    if (!newPassword || !confirmPassword) {
      return null;
    }

    return newPassword === confirmPassword ? null : { passwordMismatch: true };
  }

  private showToast(variant: 'success' | 'danger' | 'warning' | 'info', title: string, message: string): void {
    this.toastVariant = variant;
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastShow = true;
  }

  private resolveErrorMessage(status?: number, fallbackMessage?: string): string {
    switch (status) {
      case 400:
        return fallbackMessage || 'Please correct the highlighted fields and try again.';
      case 401:
        return 'Your session has expired. Please login again.';
      case 403:
        return 'You do not have permission to perform this action.';
      case 404:
        return 'Profile data was not found.';
      case 500:
        return 'A server error occurred. Please try again later.';
      default:
        return fallbackMessage || 'An unexpected error occurred.';
    }
  }

  private applyServerErrors(form: FormGroup, errorBody: any): void {
    if (!errorBody) {
      return;
    }

    if (errorBody.fieldErrors && typeof errorBody.fieldErrors === 'object') {
      Object.entries(errorBody.fieldErrors).forEach(([key, value]) => {
        this.setServerError(form, key, String(value));
      });
    }

    if (Array.isArray(errorBody.violations)) {
      errorBody.violations.forEach((violation: any) => {
        const field = this.safeString(violation?.field);
        const message = this.safeString(violation?.message);
        if (field && message) {
          this.setServerError(form, field, message);
        }
      });
    }
  }

  private setServerError(form: FormGroup, field: string, message: string): void {
    const mappedField = this.mapServerField(field);
    const control = form.get(mappedField);
    if (!control) {
      return;
    }

    control.setErrors({ ...(control.errors || {}), server: message });
    control.markAsTouched();
  }

  private mapServerField(field: string): string {
    const normalized = this.safeString(field).toLowerCase();
    if (normalized === 'phone') {
      return 'phoneNumber';
    }
    if (normalized === 'password') {
      return 'newPassword';
    }
    if (normalized === 'oldpassword' || normalized === 'currentpassword') {
      return 'currentPassword';
    }
    if (normalized === 'newpassword') {
      return 'newPassword';
    }
    if (normalized === 'confirmpassword') {
      return 'confirmPassword';
    }
    if (normalized === 'name') {
      return 'firstName';
    }
    return normalized;
  }

  private clearServerErrors(form: FormGroup): void {
    Object.keys(form.controls).forEach((controlName) => {
      const control = form.get(controlName);
      if (!control?.errors?.['server']) {
        return;
      }

      const { server, ...rest } = control.errors;
      const nextErrors = Object.keys(rest).length > 0 ? rest : null;
      control.setErrors(nextErrors);
    });
  }

  private safeString(value: unknown): string {
    return typeof value === 'string' ? value.trim() : '';
  }
}
