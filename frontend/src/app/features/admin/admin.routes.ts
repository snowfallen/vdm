import { Routes } from '@angular/router';
import {roleAdminGuard} from '../../core/auth/guards/role.guard';

export const adminRoutes: Routes = [
  {
    path: '',
    canActivate: [roleAdminGuard],  // авторизований — завжди
    loadComponent: () =>
      import('./layout/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      {
        path: '',
        loadComponent: () =>
          import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
      },
      {
        path: 'profile',
        loadComponent: () =>
          import('./profile/profile.component').then(m => m.ProfileComponent)
      },
      // ---- Тільки ADMIN ----
      {
        path: 'categories',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./categories/categories.component').then(m => m.CategoriesComponent)
      },
      {
        path: 'sub-categories',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./sub-categories/sub-categories.component').then(m => m.SubCategoriesComponent)
      },
      {
        path: 'units',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./units/units.component').then(m => m.UnitsComponent)
      },
      {
        path: 'attributes',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./attributes/attributes.component').then(m => m.AttributesComponent)
      },
      {
        path: 'attribute-options',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./attribute-options/attribute-options.component').then(
            m => m.AttributeOptionsComponent
          )
      },
      {
        path: 'products',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./products/products.component').then(m => m.ProductsComponent)
      },
      {
        path: 'users',
        canActivate: [roleAdminGuard],
        loadComponent: () =>
          import('./users/users.component').then(m => m.UsersComponent)
      },
      {
        path: '**',
        redirectTo: ''
      }
    ]
  }
];
