import { Routes } from '@angular/router';
import { DashboardLayout } from './layouts/dashboard-layout/dashboard-layout';
import { DashboardPage } from './features/dashboard/pages/dashboard/dashboard-page';
import { AboutPage } from './features/dashboard/pages/about/about-page';
import { EmployeesPage } from './features/employees/pages/employees/employees-page';
import { EmpForm } from './features/employees/components/emp-form/emp-form';
import { EmpDetail } from './features/employees/components/emp-detail/emp-detail';

export const routes: Routes = [
  {
    path: '',
    component: DashboardLayout,
    children: [
      { path: '', component: DashboardPage },
      { path: 'about', component: AboutPage },
      { path: 'employees', component: EmployeesPage },
      { path: 'employees/add', component: EmpForm, data: { mode: 'ADD' } },
      { path: 'employees/edit/:id', component: EmpForm, data: { mode: 'UPDATE' } },
      { path: 'employees/:id', component: EmpDetail }
    ]
  },
  { path: '**', redirectTo: '' }
];
