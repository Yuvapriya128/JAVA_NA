import { CommonModule } from '@angular/common';
import { Component, OnInit, WritableSignal, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { UiStatusState, defaultUiStatus } from '../../../constants/ui-status';
import { PageHeaderComponent } from '../../shared/page-header/page-header.component';

interface UserPreferences {
  emailAlerts: boolean;
  smsAlerts: boolean;
  dueReminderDays: number;
  compactTables: boolean;
  defaultLanding: 'dashboard' | 'applications' | 'payments' | 'loans';
}

@Component({
  selector: 'app-settings-center',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, PageHeaderComponent],
  templateUrl: './settings-center.component.html',
  styleUrls: ['./settings-center.component.css']
})
export class SettingsCenterComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly storageKey = 'loanhub_user_preferences';

  readonly status: WritableSignal<UiStatusState> = signal(defaultUiStatus());
  readonly successMessage = signal('');
  readonly activeTab = signal<'preferences' | 'notifications' | 'security'>('preferences');

  readonly form = this.fb.group({
    emailAlerts: [true],
    smsAlerts: [false],
    dueReminderDays: [3, [Validators.required, Validators.min(1), Validators.max(15)]],
    compactTables: [false],
    defaultLanding: ['dashboard', [Validators.required]]
  });

  readonly securityForm = this.fb.group({
    currentPassword: ['', [Validators.required, Validators.minLength(6)]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
    confirmPassword: ['', [Validators.required, Validators.minLength(8)]]
  });

  ngOnInit(): void {
    this.loadPreferences();
  }

  setTab(tab: 'preferences' | 'notifications' | 'security'): void {
    this.activeTab.set(tab);
    this.successMessage.set('');
  }

  savePreferences(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const value = this.form.getRawValue();
    const payload: UserPreferences = {
      emailAlerts: !!value.emailAlerts,
      smsAlerts: !!value.smsAlerts,
      dueReminderDays: Number(value.dueReminderDays) || 3,
      compactTables: !!value.compactTables,
      defaultLanding: (value.defaultLanding as UserPreferences['defaultLanding']) || 'dashboard'
    };
    localStorage.setItem(this.storageKey, JSON.stringify(payload));
    this.successMessage.set('Preferences saved successfully.');
  }

  resetPreferences(): void {
    this.form.reset({
      emailAlerts: true,
      smsAlerts: false,
      dueReminderDays: 3,
      compactTables: false,
      defaultLanding: 'dashboard'
    });
    this.successMessage.set('Preferences reset to defaults.');
  }

  updatePassword(): void {
    if (this.securityForm.invalid) {
      this.securityForm.markAllAsTouched();
      return;
    }
    const values = this.securityForm.getRawValue();
    if (values.newPassword !== values.confirmPassword) {
      this.status.set({ loading: false, success: false, error: 'New password and confirmation do not match.' });
      this.successMessage.set('');
      return;
    }
    this.status.set({ loading: false, success: true, error: '' });
    this.successMessage.set('Password change request captured. Backend password API can be plugged in without UI changes.');
    this.securityForm.reset({
      currentPassword: '',
      newPassword: '',
      confirmPassword: ''
    });
  }

  private loadPreferences(): void {
    const raw = localStorage.getItem(this.storageKey);
    if (!raw) {
      return;
    }
    try {
      const parsed = JSON.parse(raw) as Partial<UserPreferences>;
      this.form.patchValue({
        emailAlerts: parsed.emailAlerts ?? true,
        smsAlerts: parsed.smsAlerts ?? false,
        dueReminderDays: parsed.dueReminderDays ?? 3,
        compactTables: parsed.compactTables ?? false,
        defaultLanding: parsed.defaultLanding ?? 'dashboard'
      });
    } catch {
      localStorage.removeItem(this.storageKey);
    }
  }
}

