import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { AuthService } from '../auth/auth.service';

export interface CheckoutLine {
  productId: number;
  quantity: number;
}

export interface OrderLine {
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
}

export interface Order {
  id: number;
  userId: number;
  totalAmount: number;
  status: string;
  createdAt: string;
  lines: OrderLine[];
}

@Injectable({ providedIn: 'root' })
export class OrderApiService {
  constructor(
    private readonly http: HttpClient,
    private readonly auth: AuthService
  ) {}

  checkout(lines: CheckoutLine[]) {
    return this.http.post<Order>(this.auth.api('/api/orders'), { lines });
  }

  history() {
    return this.http.get<Order[]>(this.auth.api('/api/orders'));
  }

  get(id: number) {
    return this.http.get<Order>(this.auth.api(`/api/orders/${id}`));
  }
}
