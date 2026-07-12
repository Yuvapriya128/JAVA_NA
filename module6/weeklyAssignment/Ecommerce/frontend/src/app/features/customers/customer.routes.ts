import { Routes } from '@angular/router';
import { CustomerDashboardComponent } from './components/customer-dashboard/customer-dashboard.component';
import { CustomerViewComponent } from './components/customer-view/customer-view.component';
import { CustomerCreateComponent } from './components/customer-create/customer-create.component';
import { CustomerEditComponent } from './components/customer-edit/customer-edit.component';

export const customerRoutes: Routes = [
  {
    path: '',
    component: CustomerDashboardComponent
  },
  {
    path: 'create',
    component: CustomerCreateComponent
  },
  {
    path: 'edit/:id',
    component: CustomerEditComponent
  },
  {
    path: ':id',
    component: CustomerViewComponent
  }
];

