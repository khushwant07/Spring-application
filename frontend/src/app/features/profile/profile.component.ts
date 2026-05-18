import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatSnackBarModule],
  template: `
    <div class="wrap">
      @if (auth.profile(); as p) {
        <mat-card>
          <mat-card-title>Profile</mat-card-title>
          <mat-card-content>
            <p><strong>Name:</strong> {{ p.firstName }} {{ p.lastName }}</p>
            <p><strong>Email:</strong> {{ p.email }}</p>
            <p><strong>Roles:</strong> {{ p.roles.join(', ') }}</p>
          </mat-card-content>
        </mat-card>
      } @else {
        <p class="pad muted">Loading profile…</p>
      }
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
export class ProfileComponent implements OnInit {
  readonly auth = inject(AuthService);
  private readonly snack = inject(MatSnackBar);

  ngOnInit() {
    this.auth.refreshProfile().subscribe({
      error: () => this.snack.open('Could not refresh profile', 'OK'),
    });
  }
}
