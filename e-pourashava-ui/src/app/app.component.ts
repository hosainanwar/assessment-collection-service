import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <nav class="navbar navbar-expand-lg navbar-dark bg-primary">
      <div class="container">
        <a class="navbar-brand" href="#">E-Pourashava</a>
        <div class="navbar-nav">
          <a class="nav-link" routerLink="/divisions" routerLinkActive="active">Divisions</a>
        </div>
      </div>
    </nav>
    <router-outlet></router-outlet>
  `,
  styles: [`
    .navbar-brand {
      font-weight: bold;
    }
  `]
})
export class AppComponent {
  title = 'E-Pourashava Assessment & Collection';
}
