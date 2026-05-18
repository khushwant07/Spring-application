import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CartService } from '../../core/cart/cart.service';
import { OrderApiService } from '../../core/orders/order-api.service';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatButtonModule, MatSnackBarModule],
  template: `
    <div class="wrap">
      <mat-card>
        <mat-card-title>Checkout</mat-card-title>
        <mat-card-content>
          <p>Total items: {{ cart.count() }}</p>
          <p *ngIf="cart.items().length === 0" class="muted">Your cart is empty.</p>
        </mat-card-content>
        <mat-card-actions>
          <button
            mat-flat-button
            color="primary"
            (click)="place()"
            [disabled]="cart.items().length === 0 || busy"
          >
            Place order
          </button>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: `
    .wrap {
      max-width: 520px;
      margin: 32px auto;
      padding: 0 16px;
    }
    .muted {
      color: #666;
    }
  `,
})
export class CheckoutComponent {
  readonly cart = inject(CartService);
  private readonly orders = inject(OrderApiService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  busy = false;

  place() {
    const lines = this.cart.items().map((i) => ({
      productId: i.productId,
      quantity: i.quantity,
    }));
    this.busy = true;
    this.orders.checkout(lines).subscribe({
      next: () => {
        this.snack.open('Order placed', 'OK', { duration: 2500 });
        this.cart.clear();
        this.router.navigate(['/orders']);
        this.busy = false;
      },
      error: () => {
        this.snack.open('Checkout failed (stock or network)', 'OK');
        this.busy = false;
      },
    });
  }
}
