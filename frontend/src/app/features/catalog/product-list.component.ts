import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatButtonModule } from '@angular/material/button';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { CatalogService, Category, Product } from '../../core/catalog/catalog.service';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatButtonModule,
    MatPaginatorModule,
    MatSnackBarModule,
  ],
  template: `
    <div class="toolbar mat-elevation-z1">
      <mat-form-field appearance="outline">
        <mat-label>Search</mat-label>
        <input matInput [(ngModel)]="q" (keyup.enter)="reload()" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Category</mat-label>
        <mat-select [(ngModel)]="selectedCategory" (selectionChange)="reload()">
          <mat-option value="">All</mat-option>
          <mat-option *ngFor="let c of categories" [value]="c.id.toString()">{{ c.name }}</mat-option>
        </mat-select>
      </mat-form-field>
      <button mat-flat-button color="primary" (click)="reload()">Apply</button>
    </div>
    <div class="grid">
      <mat-card *ngFor="let p of products" class="card">
        <img mat-card-image *ngIf="p.imageUrl" [src]="p.imageUrl" [alt]="p.name" />
        <mat-card-title>{{ p.name }}</mat-card-title>
        <mat-card-content>
          <p class="muted">{{ p.categoryName }}</p>
          <p>{{ p.price | currency }}</p>
        </mat-card-content>
        <mat-card-actions>
          <a mat-button [routerLink]="['/products', p.id]">Details</a>
        </mat-card-actions>
      </mat-card>
    </div>
    <mat-paginator
      [length]="total"
      [pageIndex]="page"
      [pageSize]="size"
      [pageSizeOptions]="[12, 24, 48]"
      (page)="onPage($event)"
    />
  `,
  styles: `
    .toolbar {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      padding: 12px 16px;
      margin-bottom: 16px;
      align-items: center;
    }
    .grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
      gap: 16px;
      padding: 0 16px 24px;
    }
    .card img {
      height: 160px;
      object-fit: cover;
    }
    .muted {
      color: #666;
      font-size: 0.9rem;
    }
  `,
})
export class ProductListComponent implements OnInit {
  private readonly catalog = inject(CatalogService);
  private readonly snack = inject(MatSnackBar);

  products: Product[] = [];
  categories: Category[] = [];
  q = '';
  selectedCategory = '';
  page = 0;
  size = 12;
  total = 0;

  ngOnInit() {
    this.catalog.categories().subscribe({
      next: (c) => (this.categories = c),
      error: () => {},
    });
    this.reload();
  }

  reload() {
    const cat = this.selectedCategory === '' ? undefined : Number(this.selectedCategory);
    this.catalog.products(this.page, this.size, this.q || undefined, cat).subscribe({
        next: (res) => {
          this.products = res.content;
          this.total = res.totalElements;
        },
        error: () => this.snack.open('Could not load products', 'OK'),
      });
  }

  onPage(ev: PageEvent) {
    this.page = ev.pageIndex;
    this.size = ev.pageSize;
    this.reload();
  }
}
