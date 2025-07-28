import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Dashboard } from './pages/dashboard/dashboard';
import { authGuard } from './core/guards/auth-guard';
import { NotAuthorized } from './pages/not-authorized/not-authorized';
import { NotFound } from './pages/not-found/not-found';
import { loginGuard } from './core/guards/login-guard';

export const routes: Routes = [
  { path: '', component: Login },
  { path: 'login', component: Login, canActivate: [loginGuard] },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'not-authorized', component: NotAuthorized },
  { path: '**', component: NotFound },
];

