import { Routes } from '@angular/router';
import { ProductDashboardComponent } from './components/product-dashboard/product-dashboard.component';
import { ProductViewComponent } from './components/product-view/product-view.component';
import { ProductCreateComponent } from './components/product-create/product-create.component';
import { ProductEditComponent } from './components/product-edit/product-edit.component';

export const productRoutes: Routes = [
  {
    path: '',
    component: ProductDashboardComponent
  },
  {
    path: 'create',
    component: ProductCreateComponent
  },
  {
    path: 'edit/:id',
    component: ProductEditComponent
  },
  {
    path: ':id',
    component: ProductViewComponent
  }
];

