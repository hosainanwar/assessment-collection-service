import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, map, tap } from 'rxjs';
import { environment } from '../../environments/environment';
import { ApiResponse } from '../common/model/api-response.model';

export interface LoginRequest {
  username: string;
  password: string;
  tenantId: string;
}

export interface LoginResponse {
  accessToken?: string;
  refreshToken?: string;
  tokenType?: string;
  expiresIn?: number;
  username: string;
  tenantId?: string;
  pourashavaId?: number;
  role?: string;
  roles?: string[];
  permissions?: string[];
  subdomain?: string;
}

export interface StoredUser {
  username: string;
  role?: string;
  roles?: string[];
  permissions?: string[];
  tenantId?: string;
  pourashavaId?: number;
  subdomain?: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {

  private readonly TOKEN_KEY = 'access_token';
  private readonly REFRESH_TOKEN_KEY = 'refresh_token';
  private readonly USER_KEY = 'current_user';

  constructor(private http: HttpClient) {}

  login(request: LoginRequest): Observable<LoginResponse> {
    return this.http.post<ApiResponse<LoginResponse>>(`${environment.apiBaseUrl}/auth/login`, request).pipe(
      map(res => res.data),
      tap(response => this.persistSession(response))
    );
  }

  refreshToken(): Observable<LoginResponse> {
    const refreshToken = localStorage.getItem(this.REFRESH_TOKEN_KEY);
    return this.http.post<ApiResponse<LoginResponse>>(`${environment.apiBaseUrl}/auth/refresh`, { refreshToken }).pipe(
      map(res => res.data),
      tap(response => this.persistSession(response))
    );
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getCurrentUser(): StoredUser | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }

  getSubdomain(): string | null {
    return this.getCurrentUser()?.subdomain || null;
  }

  isSuperAdmin(): boolean {
    const user = this.getCurrentUser();
    return !!user?.roles?.includes('SUPER_ADMIN') || user?.role === 'SUPER_ADMIN';
  }

  hasPermission(code: string): boolean {
    if (this.isSuperAdmin()) {
      return true;
    }
    return !!this.getCurrentUser()?.permissions?.includes(code);
  }

  hasAnyPermission(codes: string | string[]): boolean {
    const list = Array.isArray(codes) ? codes : [codes];
    return list.some(code => this.hasPermission(code));
  }

  private persistSession(response: LoginResponse): void {
    if (response.accessToken) {
      localStorage.setItem(this.TOKEN_KEY, response.accessToken);
    }
    if (response.refreshToken) {
      localStorage.setItem(this.REFRESH_TOKEN_KEY, response.refreshToken);
    }
    localStorage.setItem(this.USER_KEY, JSON.stringify({
      username: response.username,
      role: response.role,
      roles: response.roles || [],
      permissions: response.permissions || [],
      tenantId: response.tenantId,
      pourashavaId: response.pourashavaId,
      subdomain: response.subdomain
    } as StoredUser));
  }
}
