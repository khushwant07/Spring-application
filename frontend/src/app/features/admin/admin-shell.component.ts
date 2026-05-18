import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-admin-shell',
  standalone: true,
  imports: [
    RouterOutlet,
    RouterLink,
    RouterLinkActive,
    MatSidenavModule,
    MatListModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
  ],
  template: `
    <mat-sidenav-container class="shell">
      <mat-sidenav mode="side" opened class="nav">
        <mat-toolbar>Admin</mat-toolbar>
        <mat-nav-list>
          <a mat-list-item routerLink="/admin/products" routerLinkActive="active">Products</a>
          <a mat-list-item routerLink="/admin/categories" routerLinkActive="active">Categories</a>
        </mat-nav-list>
        <div class="pad">
          <a mat-button routerLink="/">Exit admin</a>
        </div>
      </mat-sidenav>
      <mat-sidenav-content>
        <mat-toolbar color="primary">
          <span>Dashboard</span>
        </mat-toolbar>
        <div class="content">
          <router-outlet />
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>
  `,
  styles: `
    .shell {
      height: calc(100vh - 64px);
    }
    .nav {
      width: 220px;
    }
    .pad {
      padding: 8px 16px;
    }
    .content {
      padding: 16px;
    }
    a.active {
      font-weight: 600;
    }
  `,
})
export class AdminShellComponent {}
