import { Routes } from '@angular/router';
import { LayoutComp } from './components/layout/LayoutComp';
import { WordComp } from './components/words/WordComp';
import { ParaComp } from './components/paras/ParaComp';
import { DivisionComp } from './components/division/DivisionComp';
import { DistrictComp } from './components/district/DistrictComp';
import { PourashavaComp } from './components/pourashava/PourashavaComp';
import { PouroshovaInfoComp } from './components/pouroshova-info/PouroshovaInfoComp';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComp,
    children: [
      { path: '', redirectTo: 'divisions', pathMatch: 'full' },
      { path: 'divisions', component: DivisionComp },
      { path: 'districts', component: DistrictComp },
      { path: 'pourashavas', component: PourashavaComp },
      { path: 'pouroshova-infos', component: PouroshovaInfoComp },
      { path: 'words', component: WordComp },
      { path: 'paras', component: ParaComp }
    ]
  }
];
