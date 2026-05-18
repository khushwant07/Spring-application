import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { CatalogService, Product } from '../../core/catalog/catalog.service';
import { CartService } from '../../core/cart/cart.service';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [
    CommonModule,
    RouterLink,
    MatCardModule,
    MatButtonModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
    FormsModule,
  ],
  template: `
    <div class="wrap" *ngIf="product as p">
      <mat-card>
        <img mat-card-image *ngIf="p.imageUrl" [src]="p.imageUrl" [alt]="p.name" />
        <mat-card-title>{{ p.name }}</mat-card-title>
        <mat-card-subtitle>{{ p.categoryName }}</mat-card-subtitle>
        <mat-card-content>
          <p>{{ p.description }}</p>
          <p class="price">{{ p.price | currency }}</p>
          <p>Stock: {{ p.stockQuantity }}</p>
          <mat-form-field appearance="outline">
            <mat-label>Quantity</mat-label>
            <input matInput type="number" min="1" [max]="p.stockQuantity" [(ngModel)]="qty" />
          </mat-form-field>
        </mat-card-content>
        <mat-card-actions>
          <button mat-flat-button color="primary" (click)="add(p)" [disabled]="p.stockQuantity < 1">
            Add to cart
          </button>
          <a mat-button routerLink="/products">Back</a>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: `
    .wrap {
      max-width: 640px;
      margin: 24px auto;
      padding: 0 16px;
    }
    .price {
      font-size: 1.25rem;
      font-weight: 600;
    }
  `,
})
export class ProductDetailComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly catalog = inject(CatalogService);
  private readonly cart = inject(CartService);
  private readonly snack = inject(MatSnackBar);

  product: Product | null = null;
  qty = 1;

  ngOnInit() {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.catalog.product(id).subscribe({
      next: (p) => (this.product = p),
      error: () => this.snack.open('Product not found', 'OK'),
    });
  }

  add(p: Product) {
    const q = Math.max(1, Math.min(this.qty, p.stockQuantity));
    this.cart.add(p, q);
    this.snack.open('Added to cart', 'OK', { duration: 2000 });
  }
}
