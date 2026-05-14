import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, MatButtonModule, MatCardModule],
  template: `
    <div class="hero">
      <mat-card>
        <mat-card-header>
          <mat-card-title>E-Commerce Demo</mat-card-title>
          <mat-card-subtitle>Spring Cloud microservices + Angular</mat-card-subtitle>
        </mat-card-header>
        <mat-card-content>
          <p>Browse products, manage your cart, and checkout. Admins can manage catalog data.</p>
        </mat-card-content>
        <mat-card-actions>
          <a mat-flat-button color="primary" routerLink="/products">Browse products</a>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: `
    .hero {
      max-width: 720px;
      margin: 32px auto;
      padding: 0 16px;
    }
  `,
})
export class HomeComponent {}
