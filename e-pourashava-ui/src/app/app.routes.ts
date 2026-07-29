import { Routes } from '@angular/router';
import { LayoutComp } from './components/layout/LayoutComp';
import { WordsComp } from './components/words/WordsComp';
import { ParasComp } from './components/paras/ParasComp';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComp,
    children: [
      { path: '', redirectTo: 'words', pathMatch: 'full' },
      { path: 'words', component: WordsComp },
      { path: 'paras', component: ParasComp }
    ]
  }
];
