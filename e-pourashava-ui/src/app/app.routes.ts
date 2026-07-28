import { Routes } from '@angular/router';
import { DivisionComponent } from './module/division/division.component';

export const routes: Routes = [
  { path: '', redirectTo: 'divisions', pathMatch: 'full' },
  { path: 'divisions', component: DivisionComponent },
  { path: '**', redirectTo: 'divisions' }
];
