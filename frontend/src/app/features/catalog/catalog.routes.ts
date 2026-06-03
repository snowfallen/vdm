import { Routes } from '@angular/router';

export const catalogRoutes: Routes = [
  // Підкатегорія + productGroup
  {
    path: ':categoryId/:subCategoryId/:productGroupId',
    loadComponent: () =>
      import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
  },
  // Тільки підкатегорія — показуємо всі групи + всі товари
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
  // Прямий перехід по productGroup без повного шляху
  {
    path: 'group/:productGroupId',
    loadComponent: () =>
      import('./pages/catalog/catalog.component').then(m => m.CatalogComponent)
  },
];
