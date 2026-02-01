import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./features/dashboard/pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'artists',
    loadComponent: () => import('./features/artists/pages/artist-list/artist-list.component').then(m => m.ArtistListComponent)
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];

