import { Routes } from '@angular/router';
import { OrderItemDashboardComponent } from './components/order-item-dashboard/order-item-dashboard.component';
import { OrderItemViewComponent } from './components/order-item-view/order-item-view.component';
import { OrderItemCreateComponent } from './components/order-item-create/order-item-create.component';
import { OrderItemEditComponent } from './components/order-item-edit/order-item-edit.component';

export const orderItemRoutes: Routes = [
  {
    path: '',
    component: OrderItemDashboardComponent
  },
  {
    path: 'create',
    component: OrderItemCreateComponent
  },
  {
    path: 'edit/:id',
    component: OrderItemEditComponent
  },
  {
    path: ':id',
    component: OrderItemViewComponent
  }
];

