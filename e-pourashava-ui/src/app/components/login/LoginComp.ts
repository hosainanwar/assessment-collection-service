import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../service/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <div class="logo">
            <i class="bi bi-building"></i>
          </div>
          <h2>E-Pourashava</h2>
          <p>ই-পৌরসভা সিস্টেমে লগইন করুন</p>
        </div>

        <form (ngSubmit)="onLogin()" class="login-form">
          <div class="mb-3">
            <label class="form-label">ইউজারনাম</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-person"></i></span>
              <input type="text" 
                     class="form-control" 
                     [(ngModel)]="username" 
                     name="username"
                     placeholder="ইউজারনাম লিখুন"
                     required
                     autocomplete="username">
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">পাসওয়ার্ড</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-lock"></i></span>
              <input [type]="showPassword ? 'text' : 'password'" 
                     class="form-control" 
                     [(ngModel)]="password" 
                     name="password"
                     placeholder="পাসওয়ার্ড লিখুন"
                     required
                     autocomplete="current-password">
              <button class="btn btn-outline-secondary" type="button" (click)="togglePassword()">
                <i class="bi" [ngClass]="showPassword ? 'bi-eye-slash' : 'bi-eye'"></i>
              </button>
            </div>
          </div>

          <div class="mb-3">
            <label class="form-label">পৌরসভা (সাবডোমেইন)</label>
            <div class="input-group">
              <span class="input-group-text"><i class="bi bi-building"></i></span>
              <input type="text"
                     class="form-control"
                     [(ngModel)]="tenantId"
                     name="tenantId"
                     placeholder="যেমন: sreepur"
                     required
                     autocomplete="off">
            </div>
          </div>

          <div class="mb-3 form-check">
            <input type="checkbox" class="form-check-input" id="rememberMe" [(ngModel)]="rememberMe" name="rememberMe">
            <label class="form-check-label" for="rememberMe">মনে রাখুন</label>
          </div>

          <div *ngIf="errorMessage" class="alert alert-danger">
            <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
          </div>

          <button type="submit" class="btn btn-primary w-100" [disabled]="loading">
            <span *ngIf="loading" class="spinner-border spinner-border-sm me-2"></span>
            {{ loading ? 'লগইন হচ্ছে...' : 'লগইন করুন' }}
          </button>
        </form>

        <div class="login-footer">
          <p class="text-muted small">
            অ্যাডমিন: <code>admin</code> / <code>admin123</code> / <code>sreepur</code><br>
            সুপার অ্যাডমিন: <code>superadmin</code> / <code>admin123</code> / <code>demo</code>
          </p>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .login-container {
      min-height: 100vh;
      display: flex;
      align-items: center;
      justify-content: center;
      background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
      padding: 20px;
    }
    .login-card {
      background: white;
      border-radius: 16px;
      box-shadow: 0 20px 60px rgba(0,0,0,0.3);
      padding: 40px;
      width: 100%;
      max-width: 400px;
    }
    .login-header {
      text-align: center;
      margin-bottom: 30px;
    }
    .logo {
      width: 80px;
      height: 80px;
      background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      margin: 0 auto 15px;
    }
    .logo i {
      font-size: 36px;
      color: white;
    }
    .login-header h2 {
      margin: 0;
      color: #1e3c72;
      font-weight: 700;
    }
    .login-header p {
      margin: 5px 0 0;
      color: #666;
    }
    .login-form .input-group-text {
      background: #f8f9fa;
      border-right: none;
    }
    .login-form .form-control {
      border-left: none;
    }
    .login-form .form-control:focus {
      box-shadow: none;
      border-color: #dee2e6;
    }
    .login-form .input-group:focus-within .form-control,
    .login-form .input-group:focus-within .input-group-text {
      border-color: #86b7fe;
    }
    .btn-primary {
      background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
      border: none;
      padding: 12px;
      font-weight: 500;
    }
    .btn-primary:hover {
      background: linear-gradient(135deg, #152c5a 0%, #1e4680 100%);
    }
    .btn-primary:disabled {
      background: #6c757d;
    }
    .login-footer {
      text-align: center;
      margin-top: 20px;
      padding-top: 20px;
      border-top: 1px solid #eee;
    }
    .form-check-label {
      font-size: 14px;
    }
  `]
})
export class LoginComp {
  username = '';
  password = '';
  tenantId = this.inferTenant();
  showPassword = false;
  rememberMe = false;
  loading = false;
  errorMessage = '';

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  onLogin() {
    this.errorMessage = '';
    
    if (!this.username.trim() || !this.password.trim() || !this.tenantId.trim()) {
      this.errorMessage = 'ইউজারনাম, পাসওয়ার্ড এবং পৌরসভা আবশ্যক';
      return;
    }

    this.loading = true;

    this.authService.login({
      username: this.username.trim(),
      password: this.password.trim(),
      tenantId: this.tenantId.trim()
    }).subscribe({
      next: () => {
        this.loading = false;
        this.router.navigate(['/divisions']);
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error?.message || 'ইউজারনাম, পাসওয়ার্ড বা পৌরসভা ভুল';
      }
    });
  }

  private inferTenant(): string {
    const host = window.location.hostname;
    if (host && host !== 'localhost' && host !== '127.0.0.1' && host.includes('.')) {
      return host.split('.')[0];
    }
    return 'sreepur';
  }
}