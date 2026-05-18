import { HttpInterceptorFn, HttpErrorResponse, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/**
 * Catalog reads are permitAll on the backend, but Spring OAuth2 still validates a
 * Bearer token if present. Tokens issued by auth (JJWT) must decode identically on
 * every resource server (Nimbus); omitting Authorization on these public GETs avoids
 * spurious 401s after login while keeping JWT on protected routes (orders, admin, /me).
 */
function isPublicCatalogRead(req: HttpRequest<unknown>): boolean {
  if (req.method !== 'GET') return false;
  const u = req.url;
  return u.includes('/api/products') || u.includes('/api/categories');
}

export const jwtInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const snack = inject(MatSnackBar);
  const token = auth.token();
  let out = req;
  if (
    token &&
    req.url.includes('/api/') &&
    !req.url.includes('/api/auth/login') &&
    !req.url.includes('/api/auth/register') &&
    !isPublicCatalogRead(req)
  ) {
    out = req.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }
  return next(out).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401) {
        auth.logout();
        router.navigate(['/login']);
      }
      if (err.status === 429) {
        snack.open('Too many requests. Please slow down and try again.', 'Dismiss', {
          duration: 6000,
        });
      }
      return throwError(() => err);
    })
  );
};
