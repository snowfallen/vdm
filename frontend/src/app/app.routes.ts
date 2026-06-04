import { Routes } from '@angular/router';
import {canActivateRoute} from './core/auth/guards/auth.guard';
import {roleClientGuard} from './core/auth/guards/role.guard';

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
  // ← КОШИК — тільки для CLIENT
  {
    path: 'cart',
    canActivate: [roleClientGuard],
    loadComponent: () =>
      import('./features/cart/cart.component').then(m => m.CartComponent)
  },
  // ← КАБІНЕТ КЛІЄНТА
  {
    path: 'cabinet',
    canActivate: [canActivateRoute],
    loadComponent: () =>
      import('./features/client/cabinet/cabinet.component').then(m => m.CabinetComponent)
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
    path: 'search',
    loadComponent: () =>
        import('./shared/components/search.component').then(m => m.SearchComponent)
  },
  { path: 'about',    loadComponent: () => import('./features/pages/about.component').then(m => m.AboutComponent) },
  { path: 'delivery', loadComponent: () => import('./features/pages/delivery.component').then(m => m.DeliveryComponent) },
  { path: 'brands',   loadComponent: () => import('./features/pages/brands.component').then(m => m.BrandsComponent) },
  { path: 'contacts', loadComponent: () => import('./features/pages/contacts.component').then(m => m.ContactsComponent) },
  { path: 'certificates', loadComponent: () => import('./features/pages/certificates.component').then(m => m.CertificatesComponent) },
  {
    path: '**',
    redirectTo: ''
  }
];
