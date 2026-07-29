import { Routes } from '@angular/router';
import { LayoutComp } from './components/layout/LayoutComp';
import { LoginComp } from './components/login/LoginComp';
import { WordComp } from './components/words/WordComp';
import { ParaComp } from './components/paras/ParaComp';
import { DivisionComp } from './components/division/DivisionComp';
import { DistrictComp } from './components/district/DistrictComp';
import { PourashavaComp } from './components/pourashava/PourashavaComp';
import { PouroshovaInfoComp } from './components/pouroshova-info/PouroshovaInfoComp';
import { authGuard } from './common/guard/auth.guard';

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
      { path: 'divisions', component: DivisionComp },
      { path: 'districts', component: DistrictComp },
      { path: 'pourashavas', component: PourashavaComp },
      { path: 'pouroshova-infos', component: PouroshovaInfoComp },
      { path: 'words', component: WordComp },
      { path: 'paras', component: ParaComp }
    ]
  },
  { path: '**', redirectTo: '' }
];
