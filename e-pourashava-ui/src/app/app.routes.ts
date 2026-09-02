import { Routes } from '@angular/router';
import { LayoutComp } from './components/layout/LayoutComp';
import { LoginComp } from './components/login/LoginComp';
import { authGuard } from './common/guard/auth.guard';

import { DivisionListComponent } from './components/division/DivisionListComp';
import { DivisionFormComponent } from './components/division/DivisionFormComp';

import { DistrictListComponent } from './components/district/DistrictListComp';
import { DistrictFormComponent } from './components/district/DistrictFormComp';

import { PourashavaListComponent } from './components/pourashava/PourashavaListComp';
import { PourashavaFormComponent } from './components/pourashava/PourashavaFormComp';

import { PouroshovaInfoComp } from './components/pouroshova-info/PouroshovaInfoComp';

import { WordListComponent } from './components/words/WordListComp';
import { WordFormComponent } from './components/words/WordFormComp';

import { ParaListComponent } from './components/paras/ParaListComp';
import { ParaFormComponent } from './components/paras/ParaFormComp';

import { UserListComponent } from './components/user/UserListComp';
import { UserFormComponent } from './components/user/UserFormComp';
import { RoleListComponent } from './components/role/RoleListComp';
import { RoleFormComponent } from './components/role/RoleFormComp';

export const routes: Routes = [
  {
    path: 'login',
    component: LoginComp
  },
  {
    path: '',
    component: LayoutComp,
    canActivate: [authGuard],
    children: [
      { path: '', redirectTo: 'divisions', pathMatch: 'full' },
      
      // Division routes
      { path: 'divisions', component: DivisionListComponent },
      { path: 'divisions/create', component: DivisionFormComponent },
      { path: 'divisions/edit/:id', component: DivisionFormComponent },
      
      // District routes
      { path: 'districts', component: DistrictListComponent },
      { path: 'districts/create', component: DistrictFormComponent },
      { path: 'districts/edit/:id', component: DistrictFormComponent },
      
      // Pourashava routes
      { path: 'pourashavas', component: PourashavaListComponent },
      { path: 'pourashavas/create', component: PourashavaFormComponent },
      { path: 'pourashavas/edit/:id', component: PourashavaFormComponent },
      
      // PouroshovaInfo routes
      { path: 'pouroshova-infos', component: PouroshovaInfoComp },
      
      // Word routes
      { path: 'words', component: WordListComponent },
      { path: 'words/create', component: WordFormComponent },
      { path: 'words/edit/:id', component: WordFormComponent },
      
      // Para routes
      { path: 'paras', component: ParaListComponent },
      { path: 'paras/create', component: ParaFormComponent },
      { path: 'paras/edit/:id', component: ParaFormComponent },
      
      // User routes
      { path: 'users', component: UserListComponent },
      { path: 'users/create', component: UserFormComponent },
      { path: 'users/edit/:id', component: UserFormComponent },

      { path: 'roles', component: RoleListComponent },
      { path: 'roles/create', component: RoleFormComponent },
      { path: 'roles/edit/:id', component: RoleFormComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
