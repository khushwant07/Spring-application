import { Routes } from '@angular/router';

export const ADMIN_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./admin-shell.component').then((m) => m.AdminShellComponent),
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'products' },
      {
        path: 'products',
        loadComponent: () =>
          import('./admin-products.component').then((m) => m.AdminProductsComponent),
      },
      {
        path: 'categories',
        loadComponent: () =>
          import('./admin-categories.component').then((m) => m.AdminCategoriesComponent),
      },
    ],
  },
];
