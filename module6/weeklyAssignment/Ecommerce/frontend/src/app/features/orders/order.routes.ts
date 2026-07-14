import { Routes } from '@angular/router';
import { OrderDashboardComponent } from './components/order-dashboard/order-dashboard.component';
import { OrderViewComponent } from './components/order-view/order-view.component';
import { OrderCreateComponent } from './components/order-create/order-create.component';
import { OrderEditComponent } from './components/order-edit/order-edit.component';

export const orderRoutes: Routes = [
  {
    path: '',
    component: OrderDashboardComponent
  },
  {
    path: 'create',
    component: OrderCreateComponent
  },
  {
    path: 'edit/:id',
    component: OrderEditComponent
  },
  {
    path: ':id',
    component: OrderViewComponent
  }
];

