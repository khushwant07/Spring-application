import { Injectable, signal, computed } from '@angular/core';
import type { Product } from '../catalog/catalog.service';

export interface CartLine {
  productId: number;
  name: string;
  unitPrice: number;
  quantity: number;
  imageUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private readonly lines = signal<CartLine[]>([]);

  readonly items = computed(() => this.lines());
  readonly count = computed(() =>
    this.lines().reduce((s, l) => s + l.quantity, 0)
  );

  add(product: Product, qty = 1) {
    this.lines.update((cur) => {
      const i = cur.findIndex((l) => l.productId === product.id);
      const price = Number(product.price);
      if (i >= 0) {
        const copy = [...cur];
        const line = { ...copy[i] };
        line.quantity = Math.min(line.quantity + qty, product.stockQuantity);
        copy[i] = line;
        return copy;
      }
      return [
        ...cur,
        {
          productId: product.id,
          name: product.name,
          unitPrice: price,
          quantity: Math.min(qty, product.stockQuantity),
          imageUrl: product.imageUrl,
        },
      ];
    });
  }

  setQty(productId: number, quantity: number) {
    this.lines.update((cur) =>
      cur
        .map((l) => (l.productId === productId ? { ...l, quantity: Math.max(1, quantity) } : l))
        .filter((l) => l.quantity > 0)
    );
  }

  remove(productId: number) {
    this.lines.update((cur) => cur.filter((l) => l.productId !== productId));
  }

  clear() {
    this.lines.set([]);
  }
}
