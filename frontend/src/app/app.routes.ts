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
    path: 'artists/:id',
    loadComponent: () => import('./features/artists/pages/artist-detail/artist-detail.component').then(m => m.ArtistDetailComponent)
  },
  {
    path: '**',
    redirectTo: '/home'
  }
];

