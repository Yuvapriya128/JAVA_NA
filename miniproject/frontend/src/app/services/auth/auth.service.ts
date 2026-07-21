import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, catchError, tap } from 'rxjs';
import { ChangePasswordRequest, ChangePasswordResponse, LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from '../../dto/auth/auth.dto';
import { CustomerResponse } from '../../dto/customer/customer.dto';
import { environment } from '../../../environments/environment';
import { handleHttpError } from '../shared/http-error-handler';
import { TokenStorageService } from './token-storage.service';
export type { ChangePasswordRequest, ChangePasswordResponse, LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from '../../dto/auth/auth.dto';

interface TokenPayload {
  sub: string;
  roles: string[];
  iat: number;
  exp: number;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly tokenStorage = inject(TokenStorageService);
  private readonly baseUrl = environment.apiUrl;
  private currentUser: CustomerResponse | null = null;

  /**
   * Authenticates a user and returns a JWT token payload.
   */
  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.baseUrl}/auth/login`, request)
      .pipe(
        tap((response) => {
          this.tokenStorage.saveToken(response.token);
          this.loadCurrentUser();
        }),
        catchError(handleHttpError('Login request'))
      );
  }

  /**
   * Registers a new customer as USER only.
   */
  register(request: RegisterRequest): Observable<RegisterResponse> {
    const payload: RegisterRequest = {
      customerName: request.customerName,
      email: request.email,
      password: request.password,
      phoneNumber: request.phoneNumber,
      city: request.city
    };

    return this.http
      .post<RegisterResponse>(`${this.baseUrl}/auth/register`, payload)
      .pipe(catchError(handleHttpError('Register request')));
  }

  /**
   * Gets current authenticated user profile.
   */
  getCurrentUser(): Observable<CustomerResponse> {
    return this.http
      .get<CustomerResponse>(`${this.baseUrl}/customers/me`)
      .pipe(
        tap((user) => {
          this.currentUser = user;
        }),
        catchError(handleHttpError('Get current user request'))
      );
  }

  /**
   * Loads current user (internal use).
   */
  private loadCurrentUser(): void {
    const token = this.tokenStorage.getToken();
    if (token) {
      this.getCurrentUser().subscribe();
    }
  }

  /**
   * Gets cached current user (synchronously).
   */
  getCurrentUserSync(): CustomerResponse | null {
    return this.currentUser;
  }

  /**
   * Changes authenticated user's password.
   */
  changePassword(request: ChangePasswordRequest): Observable<ChangePasswordResponse> {
    return this.http
      .post<ChangePasswordResponse>(`${this.baseUrl}/auth/change-password`, request)
      .pipe(catchError(handleHttpError('Change password request')));
  }

  /**
   * Extracts roles from JWT token.
   */
  getRoles(): string[] {
    const token = this.tokenStorage.getToken();
    if (!token) {
      return [];
    }
    try {
      const payload = this.decodeToken(token);
      return payload.roles || [];
    } catch {
      return [];
    }
  }

  /**
   * Checks if user has a specific role.
   */
  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  /**
   * Checks if user is admin.
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Checks if user is manager.
   */
  isManager(): boolean {
    return this.hasRole('MANAGER');
  }

  /**
   * Checks if user is regular user.
   */
  isUser(): boolean {
    return this.hasRole('USER');
  }

  /**
   * Checks if user has any of the specified roles.
   */
  hasAnyRole(roles: string[]): boolean {
    return roles.some((role) => this.hasRole(role));
  }

  /**
   * Decodes JWT token.
   */
  private decodeToken(token: string): TokenPayload {
    const parts = token.split('.');
    if (parts.length !== 3) {
      throw new Error('Invalid token');
    }
    const payload = parts[1];
    const decoded = JSON.parse(atob(payload));
    return decoded as TokenPayload;
  }
}
