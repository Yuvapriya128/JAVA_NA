import { Routes } from '@angular/router';
import {App} from './app';
import {Home} from './components/home/home';
import {About} from './components/about/about';
import {Services} from './components/services/services';
import {Contact} from './components/contact/contact';
import {Person} from './components/person/person';

export const routes: Routes = [
  {path: '', component: Home},//this is eager loading
  {path: 'about', component: About},
  {path: 'services', component: Services},
  {path:'contact',loadComponent: () => import('./components/contact/contact').then(m => m.Contact)},
  {
    path:'person/:id',component:Person
  },
//   this is lazy loading
//   {path:'contact',loadComponent: () => import('./components/contact/contact').then(m => m.Contact)}
//redirect always give atlast
  {path: '**', redirectTo: ''}
];
