import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'tracks' },
  {
    path: 'tracks',
    loadComponent: () =>
      import('./features/tracks/track-list/track-list').then((m) => m.TrackList),
  },
  {
    path: 'tracks/create',
    loadComponent: () =>
      import('./features/tracks/track-create/track-create').then((m) => m.TrackCreate),
  },
  {
    path: 'tracks/search',
    loadComponent: () =>
      import('./features/tracks/track-search/track-search').then((m) => m.TrackSearch),
  },
  { path: '**', loadComponent: () => import('./features/not-found/not-found').then((m) => m.NotFound) },
];
