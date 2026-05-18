import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CatalogService, Product } from '../../core/catalog/catalog.service';

@Component({
  selector: 'app-admin-products',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  template: `
    <h2>Products</h2>
    <table mat-table [dataSource]="rows" class="mat-elevation-z1 full">
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef>Name</th>
        <td mat-cell *matCellDef="let p">{{ p.name }}</td>
      </ng-container>
      <ng-container matColumnDef="price">
        <th mat-header-cell *matHeaderCellDef>Price</th>
        <td mat-cell *matCellDef="let p">{{ p.price | currency }}</td>
      </ng-container>
      <ng-container matColumnDef="stock">
        <th mat-header-cell *matHeaderCellDef>Stock</th>
        <td mat-cell *matCellDef="let p">{{ p.stockQuantity }}</td>
      </ng-container>
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let p">
          <button mat-icon-button (click)="promptStock(p)" aria-label="Inventory">
            <mat-icon>inventory</mat-icon>
          </button>
          <button mat-icon-button color="warn" (click)="remove(p)" aria-label="Delete">
            <mat-icon>delete</mat-icon>
          </button>
        </td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="cols"></tr>
      <tr mat-row *matRowDef="let row; columns: cols"></tr>
    </table>
  `,
  styles: `
    .full {
      width: 100%;
    }
  `,
})
export class AdminProductsComponent implements OnInit {
  private readonly catalog = inject(CatalogService);
  private readonly snack = inject(MatSnackBar);

  rows: Product[] = [];
  cols = ['name', 'price', 'stock', 'actions'];

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.catalog.products(0, 200).subscribe({
      next: (p) => (this.rows = p.content),
      error: () => this.snack.open('Failed to load products', 'OK'),
    });
  }

  promptStock(p: Product) {
    const v = window.prompt('New stock quantity', String(p.stockQuantity));
    if (v == null) return;
    const n = Number(v);
    if (Number.isNaN(n) || n < 0) {
      this.snack.open('Invalid quantity', 'OK');
      return;
    }
    this.catalog.adminPatchInventory(p.id, n).subscribe({
      next: () => {
        this.snack.open('Inventory updated', 'OK', { duration: 2000 });
        this.reload();
      },
      error: () => this.snack.open('Update failed', 'OK'),
    });
  }

  remove(p: Product) {
    if (!confirm(`Delete ${p.name}?`)) return;
    this.catalog.adminDeleteProduct(p.id).subscribe({
      next: () => {
        this.snack.open('Deleted', 'OK', { duration: 2000 });
        this.reload();
      },
      error: () => this.snack.open('Delete failed', 'OK'),
    });
  }
}
