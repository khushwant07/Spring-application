import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { CatalogService, Category } from '../../core/catalog/catalog.service';

@Component({
  selector: 'app-admin-categories',
  standalone: true,
  imports: [
    CommonModule,
    FormsModule,
    MatTableModule,
    MatButtonModule,
    MatSnackBarModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  template: `
    <h2>Categories</h2>
    <div class="row mat-elevation-z1 pad">
      <mat-form-field appearance="outline">
        <mat-label>New category name</mat-label>
        <input matInput [(ngModel)]="newName" />
      </mat-form-field>
      <mat-form-field appearance="outline">
        <mat-label>Description</mat-label>
        <input matInput [(ngModel)]="newDesc" />
      </mat-form-field>
      <button mat-flat-button color="primary" (click)="create()">Add</button>
    </div>
    <table mat-table [dataSource]="rows" class="mat-elevation-z1 full">
      <ng-container matColumnDef="name">
        <th mat-header-cell *matHeaderCellDef>Name</th>
        <td mat-cell *matCellDef="let c">{{ c.name }}</td>
      </ng-container>
      <ng-container matColumnDef="description">
        <th mat-header-cell *matHeaderCellDef>Description</th>
        <td mat-cell *matCellDef="let c">{{ c.description }}</td>
      </ng-container>
      <ng-container matColumnDef="actions">
        <th mat-header-cell *matHeaderCellDef></th>
        <td mat-cell *matCellDef="let c">
          <button mat-button color="warn" (click)="remove(c)">Delete</button>
        </td>
      </ng-container>
      <tr mat-header-row *matHeaderRowDef="cols"></tr>
      <tr mat-row *matRowDef="let row; columns: cols"></tr>
    </table>
  `,
  styles: `
    .full {
      width: 100%;
      margin-top: 16px;
    }
    .row {
      display: flex;
      flex-wrap: wrap;
      gap: 12px;
      align-items: center;
      padding: 12px;
    }
    .pad mat-form-field {
      min-width: 200px;
    }
  `,
})
export class AdminCategoriesComponent implements OnInit {
  private readonly catalog = inject(CatalogService);
  private readonly snack = inject(MatSnackBar);

  rows: Category[] = [];
  cols = ['name', 'description', 'actions'];
  newName = '';
  newDesc = '';

  ngOnInit() {
    this.reload();
  }

  reload() {
    this.catalog.categories().subscribe({
      next: (c) => (this.rows = c),
      error: () => this.snack.open('Failed to load categories', 'OK'),
    });
  }

  create() {
    if (!this.newName.trim()) return;
    this.catalog
      .adminCreateCategory({ name: this.newName.trim(), description: this.newDesc || undefined })
      .subscribe({
        next: () => {
          this.snack.open('Created', 'OK', { duration: 2000 });
          this.newName = '';
          this.newDesc = '';
          this.reload();
        },
        error: () => this.snack.open('Create failed', 'OK'),
      });
  }

  remove(c: Category) {
    if (!confirm(`Delete category ${c.name}?`)) return;
    this.catalog.adminDeleteCategory(c.id).subscribe({
      next: () => {
        this.snack.open('Deleted', 'OK', { duration: 2000 });
        this.reload();
      },
      error: () => this.snack.open('Delete failed (products may still reference it)', 'OK'),
    });
  }
}
