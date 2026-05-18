import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { AuthService } from '../../core/auth/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [
    ReactiveFormsModule,
    RouterLink,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSnackBarModule,
  ],
  template: `
    <div class="wrap">
      <mat-card>
        <mat-card-title>Register</mat-card-title>
        <mat-card-content [formGroup]="form">
          <mat-form-field appearance="outline" class="full">
            <mat-label>First name</mat-label>
            <input matInput formControlName="firstName" />
          </mat-form-field>
          <mat-form-field appearance="outline" class="full">
            <mat-label>Last name</mat-label>
            <input matInput formControlName="lastName" />
          </mat-form-field>
          <mat-form-field appearance="outline" class="full">
            <mat-label>Email</mat-label>
            <input matInput type="email" formControlName="email" />
          </mat-form-field>
          <mat-form-field appearance="outline" class="full">
            <mat-label>Password</mat-label>
            <input matInput type="password" formControlName="password" />
          </mat-form-field>
        </mat-card-content>
        <mat-card-actions>
          <button mat-flat-button color="primary" (click)="submit()" [disabled]="form.invalid">
            Create account
          </button>
          <a mat-button routerLink="/login">Back to login</a>
        </mat-card-actions>
      </mat-card>
    </div>
  `,
  styles: `
    .wrap {
      max-width: 420px;
      margin: 48px auto;
      padding: 0 16px;
    }
    .full {
      width: 100%;
    }
  `,
})
export class RegisterComponent {
  private readonly fb = inject(FormBuilder);
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);
  private readonly snack = inject(MatSnackBar);

  form = this.fb.nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
  });

  submit() {
    if (this.form.invalid) return;
    this.auth.register(this.form.getRawValue()).subscribe({
      next: () => {
        this.snack.open('Account created — please sign in', 'OK', { duration: 4000 });
        this.router.navigate(['/login']);
      },
      error: () => this.snack.open('Registration failed', 'OK'),
    });
  }
}
