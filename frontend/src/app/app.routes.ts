import { Routes } from '@angular/router';
import { authGuard } from './core/auth/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'home',
    loadComponent: () => import('./features/dashboard/pages/home/home.component').then(m => m.HomeComponent),
    canActivate: [authGuard]
  },
  {
    path: 'artists',
    loadComponent: () => import('./features/artists/pages/artist-list/artist-list.component').then(m => m.ArtistListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'artists/:id',
    loadComponent: () => import('./features/artists/pages/artist-detail/artist-detail.component').then(m => m.ArtistDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];

