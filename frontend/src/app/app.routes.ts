import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/home/pages/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/pages/signup/signup.component').then(m => m.SignupComponent)
  },
  {
    path: 'products/:id',
    loadComponent: () =>
      import('./features/product-detail/product-detail.component')
        .then(m => m.ProductDetailComponent)
  },
  {
    path: 'catalog',
    loadChildren: () =>
      import('./features/catalog/catalog.routes').then(m => m.catalogRoutes)
  },
  {
    path: 'admin',
    loadChildren: () =>
      import('./features/admin/admin.routes').then(m => m.adminRoutes)
  },
  {
    path: '**',
    redirectTo: ''
  }
];
