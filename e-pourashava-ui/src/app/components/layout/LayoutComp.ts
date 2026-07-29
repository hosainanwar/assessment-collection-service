import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="d-flex" style="min-height: 100vh;">
      <nav class="bg-dark text-white p-3" style="width: 250px;">
        <h5 class="mb-4">ই-পৌরশবা</h5>
        <ul class="nav flex-column">
          <li class="nav-item">
            <a class="nav-link text-white" routerLink="/words" routerLinkActive="active">ওয়ার্ড</a>
          </li>
          <li class="nav-item">
            <a class="nav-link text-white" routerLink="/paras" routerLinkActive="active">পাড়া</a>
          </li>
        </ul>
      </nav>
      <main class="flex-grow-1 p-4">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .nav-link.active {
      background-color: rgba(255,255,255,0.1);
      border-radius: 4px;
    }
  `]
})
export class LayoutComp {}
