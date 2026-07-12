import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { ApiClientService } from '../http/api-client.service';
import { API_ENDPOINTS } from '../http/endpoints';
import { AuthRequestDTO, AuthResponseDTO, ErrorResponseDTO } from './dto/auth.dto';
import { AuthStateService, AuthUser } from './auth-state.service';
import { TokenStorageService } from './token-storage.service';
import { JwtPayload, JwtService } from './jwt.service';

export type AuthStatus = 'idle' | 'loading' | 'success' | 'error';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly router = inject(Router);
  private readonly authState = inject(AuthStateService);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly jwtService = inject(JwtService);
  private readonly apiClient = inject(ApiClientService);

  private readonly authStatusSignal = signal<AuthStatus>('idle');
  private readonly errorMessageSignal = signal<string>('');
  private readonly fieldErrorsSignal = signal<Record<string, string>>({});

  readonly authStatus = this.authStatusSignal.asReadonly();
  readonly errorMessage = this.errorMessageSignal.asReadonly();
  readonly fieldErrors = this.fieldErrorsSignal.asReadonly();

  readonly currentUser = this.authState.currentUser;
  readonly token = this.authState.token;
  readonly isAuthenticated = computed(() => this.authState.isAuthenticated());

  constructor() {
    this.restoreSession();
  }

  login(credentials: AuthRequestDTO): Observable<AuthResponseDTO> {
    this.authStatusSignal.set('loading');
    this.clearErrors();

    return new Observable((observer) => {
      this.apiClient.post<AuthResponseDTO>(`${API_ENDPOINTS.AUTH}/login`, credentials).subscribe({
        next: (response) => {
          console.log('RAW LOGIN RESPONSE', response);
          const token = this.extractToken(response);
          if (!token) {
            this.handleError('No token provided by server', response);
            observer.error(new Error('No token in response'));
            return;
          }

          const decoded = this.jwtService.decodeToken(token);
          const user = this.createUserFromToken(token);

          this.logDecodedToken(token, decoded);

          this.tokenStorage.setToken(token);
          console.log('[AuthService] storedToken:', this.tokenStorage.getToken());
          this.logStorageSnapshot();

          this.authState.setCurrentUser(user);

          this.authStatusSignal.set('success');
          observer.next(response);
          observer.complete();
        },
        error: (error) => {
          this.handleError(error?.error, error);
          observer.error(error);
        }
      });
    });
  }

  logout(): void {
    this.authStatusSignal.set('idle');
    this.clearErrors();
    this.tokenStorage.clearAll();
    this.authState.reset();
    this.router.navigate(['/auth/login'], { replaceUrl: true });
  }

  resetAuthStatus(): void {
    this.authStatusSignal.set('idle');
    this.clearErrors();
  }

  getToken(): string {
    return this.tokenStorage.getToken();
  }

  hasValidSessionToken(): boolean {
    const token = this.getToken();
    return !!token && !this.jwtService.isExpired(token);
  }

  hasRole(role: string): boolean {
    return this.authState.hasRole(role);
  }

  hasPermission(permission: string): boolean {
    return this.authState.hasPermission(permission);
  }

  switchToUserView(): boolean {
    return this.authState.switchToUserView();
  }

  switchToAdminView(): boolean {
    return this.authState.switchToAdminView();
  }

  getRealRole(): string {
    return this.authState.realRole();
  }

  getEffectiveRole(): string {
    return this.authState.effectiveRole();
  }

  getCurrentUserId(): number | null {
    const user = this.currentUser();
    const resolved = Number(user?.customerId);
    return Number.isFinite(resolved) && resolved > 0 ? resolved : null;
  }

  private restoreSession(): void {
    const token = this.tokenStorage.getToken();
    if (!token) {
      return;
    }

    if (this.jwtService.isExpired(token)) {
      this.logout();
      return;
    }

    const decoded = this.jwtService.decodeToken(token);
    const user = this.createUserFromToken(token);
    this.logDecodedToken(token, decoded);
    this.authState.setCurrentUser(user);
  }

  private extractToken(response: AuthResponseDTO | null): string {
    if (!response) {
      return '';
    }

    return response.token
      || response.accessToken
      || response.jwt
      || '';
  }

  private createUserFromToken(token: string): AuthUser {
    return {
      token,
      role: this.jwtService.getRole(token),
      customerId: this.jwtService.getCustomerId(token),
      email: this.jwtService.getEmail(token),
      name: this.jwtService.getName(token)
    };
  }

  private logDecodedToken(token: string, payload: JwtPayload | null): void {
    console.log('Decoded JWT:', payload);
    console.log('Role:', this.jwtService.getRole(token));
    console.log('CustomerId:', this.jwtService.getCustomerId(token));
    console.log('Email:', this.jwtService.getEmail(token));
    console.log('Name:', this.jwtService.getName(token));
  }

  private logStorageSnapshot(): void {
    if (typeof globalThis === 'undefined') {
      return;
    }

    try {
      console.log('[AuthService] sessionStorage.auth_token:', globalThis.sessionStorage?.getItem('auth_token'));
    } catch {
      // Ignore storage logging errors in restricted browser modes.
    }
  }

  private handleError(errorData: unknown, fallback?: unknown): void {
    this.authStatusSignal.set('error');
    this.clearErrors();

    const errorResponse = errorData as ErrorResponseDTO | null;
    if (errorResponse?.fieldErrors && typeof errorResponse.fieldErrors === 'object') {
      this.fieldErrorsSignal.set(errorResponse.fieldErrors);
    }

    if (Array.isArray(errorResponse?.violations)) {
      const fieldMap: Record<string, string> = {};
      for (const violation of errorResponse.violations) {
        if (violation.field && violation.message) {
          fieldMap[violation.field] = violation.message;
        }
      }
      this.fieldErrorsSignal.set(fieldMap);
    }

    this.errorMessageSignal.set(
      errorResponse?.message ||
        errorResponse?.error ||
        (fallback ? 'Login failed. Please check your credentials.' : 'Login failed.')
    );
  }

  private clearErrors(): void {
    this.errorMessageSignal.set('');
    this.fieldErrorsSignal.set({});
  }
}
