import { Routes } from '@angular/router';

export const catalogRoutes: Routes = [
  // ← НОВИЙ: /catalog — лендінг з усіма категоріями
  {
    path: '',
    loadComponent: () =>
      import('./pages/catalog-home/catalog-home.component').then(m => m.CatalogHomeComponent)
  },

  // Підкатегорія + productGroup
  {
    path: ':categoryId/:subCategoryId/:productGroupId',
    loadComponent: () =>
      import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  // Тільки підкатегорія
  {
    path: ':categoryId/:subCategoryId',
    loadComponent: () =>
      import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  // Тільки категорія
  {
    path: ':categoryId',
    loadComponent: () =>
      import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  // Прямий перехід по productGroup
  {
    path: 'group/:productGroupId',
    loadComponent: () =>
      import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
  },
];
