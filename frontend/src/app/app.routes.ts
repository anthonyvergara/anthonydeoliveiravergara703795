import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  },
  {
    path: 'home',
    loadComponent: () => import('./components/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'artists',
    loadComponent: () => import('./components/artists/artist-list.component').then(m => m.ArtistListComponent)
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];

