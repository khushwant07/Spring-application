import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Order, OrderApiService } from '../../core/orders/order-api.service';

@Component({
  selector: 'app-order-history',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatExpansionModule, MatSnackBarModule],
  template: `
    <h2 class="pad">Order history</h2>
    <mat-accordion *ngIf="orders.length; else empty">
      <mat-expansion-panel *ngFor="let o of orders">
        <mat-expansion-panel-header>
          <mat-panel-title> Order #{{ o.id }} </mat-panel-title>
          <mat-panel-description>
            {{ o.createdAt | date: 'medium' }} — {{ o.totalAmount | currency }}
          </mat-panel-description>
        </mat-expansion-panel-header>
        <ul>
          <li *ngFor="let l of o.lines">
            {{ l.productName }} × {{ l.quantity }} — {{ l.unitPrice | currency }}
          </li>
        </ul>
      </mat-expansion-panel>
    </mat-accordion>
    <ng-template #empty>
      <p class="pad muted">No orders yet.</p>
    </ng-template>
  `,
  styles: `
    .pad {
      padding: 0 16px;
    }
    .muted {
      color: #666;
    }
  `,
})
export class OrderHistoryComponent implements OnInit {
  private readonly api = inject(OrderApiService);
  private readonly snack = inject(MatSnackBar);

  orders: Order[] = [];

  ngOnInit() {
    this.api.history().subscribe({
      next: (o) => (this.orders = o),
      error: () => this.snack.open('Could not load orders', 'OK'),
    });
  }
}
