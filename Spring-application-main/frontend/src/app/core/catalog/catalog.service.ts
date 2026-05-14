import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

export interface PagedResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  page: number;
  size: number;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stockQuantity: number;
  imageUrl: string;
  categoryId: number;
  categoryName: string;
  createdAt: string;
}

export interface Category {
  id: number;
  name: string;
  description: string;
}

@Injectable({ providedIn: 'root' })
export class CatalogService {
  constructor(
    private readonly http: HttpClient,
    private readonly auth: AuthService
  ) {}

  categories() {
    return this.http.get<Category[]>(this.auth.api('/api/categories'));
  }

  products(page = 0, size = 12, q?: string, categoryId?: number) {
    let p = new HttpParams().set('page', String(page)).set('size', String(size));
    if (q) p = p.set('q', q);
    if (categoryId != null) p = p.set('categoryId', String(categoryId));
    return this.http.get<PagedResponse<Product>>(this.auth.api('/api/products'), { params: p });
  }

  product(id: number) {
    return this.http.get<Product>(this.auth.api(`/api/products/${id}`));
  }

  adminCreateProduct(body: unknown) {
    return this.http.post<Product>(this.auth.api('/api/products'), body);
  }

  adminUpdateProduct(id: number, body: unknown) {
    return this.http.put<Product>(this.auth.api(`/api/products/${id}`), body);
  }

  adminDeleteProduct(id: number) {
    return this.http.delete<void>(this.auth.api(`/api/products/${id}`));
  }

  adminPatchInventory(id: number, stockQuantity: number) {
    return this.http.patch<Product>(this.auth.api(`/api/products/${id}/inventory`), {
      stockQuantity,
    });
  }

  adminCreateCategory(body: { name: string; description?: string }) {
    return this.http.post<Category>(this.auth.api('/api/categories'), body);
  }

  adminUpdateCategory(id: number, body: { name: string; description?: string }) {
    return this.http.put<Category>(this.auth.api(`/api/categories/${id}`), body);
  }

  adminDeleteCategory(id: number) {
    return this.http.delete<void>(this.auth.api(`/api/categories/${id}`));
  }
}
