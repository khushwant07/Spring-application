import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CartService } from '../../core/cart/cart.service';

@Component({
  selector: 'app-cart-page',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatTableModule,
    MatButtonModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  template: `
    <h2 class="pad">Shopping cart</h2>
    <div *ngIf="cart.items().length === 0" class="pad muted">Your cart is empty.</div>
    <table mat-table [dataSource]="cart.items()" *ngIf="cart.items().length" class="mat-elevation-z1 full">
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef>Product</th>
        <td mat-cell *matCellDef="let row">{{ row.name }}</td>
      </ng-container>
      <ng-container matColumnDef="price">
        <th mat-header-cell *matHeaderCellDef>Price</th>
        <td mat-cell *matCellDef="let row">{{ row.unitPrice | currency }}</td>
      </ng-container>
      <ng-container matColumnDef="qty">
        <th mat-header-cell *matHeaderCellDef>Qty</th>
        <td mat-cell *matCellDef="let row">
          <mat-form-field appearance="outline" subscriptSizing="dynamic">
            <input
              matInput
              type="number"
              [ngModel]="row.quantity"
              (ngModelChange)="updateQty(row.productId, $event)"
            />
          </mat-form-field>
        </td>
      </ng-container>
      <ng-container matColumnDef="line">
        <th mat-header-cell *matHeaderCellDef>Line total</th>
        <td mat-cell *matCellDef="let row">{{ row.unitPrice * row.quantity | currency }}</td>
      </ng-container>
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let row">
          <button mat-button color="warn" (click)="cart.remove(row.productId)">Remove</button>
        </td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="cols"></tr>
      <tr mat-row *matRowDef="let row; columns: cols"></tr>
    </table>
    <div class="pad actions" *ngIf="cart.items().length">
      <a mat-flat-button color="primary" routerLink="/checkout">Checkout</a>
      <button mat-button (click)="clear()">Clear cart</button>
    </div>
  `,
  styles: `
    .pad {
      padding: 0 16px;
    }
    .full {
      width: calc(100% - 32px);
      margin: 0 16px 16px;
    }
    .actions {
      display: flex;
      gap: 12px;
      align-items: center;
    }
    .muted {
      color: #666;
    }
  `,
})
export class CartPageComponent {
  readonly cart = inject(CartService);
  private readonly snack = inject(MatSnackBar);

  cols = ['name', 'price', 'qty', 'line', 'actions'];

  updateQty(productId: number, value: string | number) {
    const n = typeof value === 'number' ? value : parseInt(String(value), 10);
    this.cart.setQty(productId, Number.isFinite(n) ? n : 1);
  }

  clear() {
    this.cart.clear();
    this.snack.open('Cart cleared', 'OK', { duration: 2000 });
  }
}
