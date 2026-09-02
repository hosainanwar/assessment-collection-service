import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../service/auth.service';
import { HasPermissionDirective } from '../../common/directive/has-permission.directive';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive, RouterOutlet, HasPermissionDirective],
  template: `
    <div class="d-flex" style="min-height: 100vh;">
      <nav class="sidebar bg-dark text-white">
        <div class="sidebar-brand p-3 border-bottom border-secondary">
          <h5 class="mb-0"><i class="bi bi-building"></i> E-Pourashava</h5>
        </div>
        <ul class="nav flex-column p-2">
          <li class="nav-item" *hasPermission="'DIVISION:READ'">
            <a class="nav-link text-white" routerLink="/divisions" routerLinkActive="active">
              <i class="bi bi-grid me-2"></i>বিভাগ
            </a>
          </li>
          <li class="nav-item" *hasPermission="'DISTRICT:READ'">
            <a class="nav-link text-white" routerLink="/districts" routerLinkActive="active">
              <i class="bi bi-geo-alt me-2"></i>জেলা
            </a>
          </li>
          <li class="nav-item" *hasPermission="'POURASHAVA:READ'">
            <a class="nav-link text-white" routerLink="/pourashavas" routerLinkActive="active">
              <i class="bi bi-bank me-2"></i>পৌরসভা
            </a>
          </li>
          <li class="nav-item" *hasPermission="'POUROSHOVA_INFO:READ'">
            <a class="nav-link text-white" routerLink="/pouroshova-infos" routerLinkActive="active">
              <i class="bi bi-info-circle me-2"></i>পৌরসভা তথ্য
            </a>
          </li>
          <li class="nav-item" *hasPermission="'WORD:READ'">
            <a class="nav-link text-white" routerLink="/words" routerLinkActive="active">
              <i class="bi bi-grid-3x3-gap me-2"></i>ওয়ার্ড
            </a>
          </li>
          <li class="nav-item" *hasPermission="'PARA:READ'">
            <a class="nav-link text-white" routerLink="/paras" routerLinkActive="active">
              <i class="bi bi-pin-map me-2"></i>পাড়া
            </a>
          </li>
          <li class="nav-item border-top border-secondary mt-2 pt-2" *hasPermission="'USER:READ'">
            <a class="nav-link text-white" routerLink="/users" routerLinkActive="active">
              <i class="bi bi-people me-2"></i>ব্যবহারকারী
            </a>
          </li>
          <li class="nav-item" *hasPermission="'ROLE:READ'">
            <a class="nav-link text-white" routerLink="/roles" routerLinkActive="active">
              <i class="bi bi-shield-lock me-2"></i>রোল
            </a>
          </li>
        </ul>
      </nav>

      <div class="flex-grow-1 d-flex flex-column">
        <header class="topbar bg-white border-bottom px-4 py-2 d-flex justify-content-between align-items-center">
          <div>
            <button class="btn btn-sm btn-outline-secondary" (click)="toggleSidebar()">
              <i class="bi bi-list"></i>
            </button>
          </div>
          <div class="d-flex align-items-center">
            <span class="me-3 text-muted" *ngIf="authService.getCurrentUser() as user">
              {{ user.username }}
            </span>
            <button class="btn btn-sm btn-outline-danger" (click)="logout()">
              <i class="bi bi-box-arrow-right me-1"></i>লগ আউট
            </button>
          </div>
        </header>

        <main class="flex-grow-1 p-4 bg-light">
          <router-outlet></router-outlet>
        </main>

        <footer class="footer bg-white border-top px-4 py-2 text-center text-muted small">
          &copy; 2026 E-Pourashava. All rights reserved.
        </footer>
      </div>
    </div>
  `,
  styles: [`
    .sidebar { width: 250px; min-height: 100vh; transition: width 0.3s ease; }
    .sidebar.collapsed { width: 60px; }
    .nav-link { border-radius: 6px; margin-bottom: 2px; transition: background-color 0.2s; }
    .nav-link:hover { background-color: rgba(255,255,255,0.1); }
    .nav-link.active { background-color: rgba(255,255,255,0.15); font-weight: 500; }
    .topbar { position: sticky; top: 0; z-index: 100; }
  `]
})
export class LayoutComp {
  sidebarCollapsed = false;
  constructor(public authService: AuthService) {}
  toggleSidebar() { this.sidebarCollapsed = !this.sidebarCollapsed; document.querySelector('.sidebar')?.classList.toggle('collapsed'); }
  logout() { this.authService.logout(); window.location.href = '/login'; }
}
