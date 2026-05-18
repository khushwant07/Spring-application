import { Injectable, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { tap } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

function rolesFromAccessToken(token: string | null): string[] {
  if (!token) return [];
  try {
    const parts = token.split('.');
    if (parts.length < 2) return [];
    const payload = JSON.parse(atob(parts[1]));
    const r = payload['roles'] as string | undefined;
    return r ? r.split(',').map((s) => s.trim()).filter(Boolean) : [];
  } catch {
    return [];
  }
}

export interface UserProfile {
  id: number;
  email: string;
  firstName: string;
  lastName: string;
  roles: string[];
}

export interface LoginResponse {
  accessToken: string;
  tokenType: string;
  expiresInMs: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly storageKey = 'access_token';
  readonly token = signal<string | null>(null);
  readonly profile = signal<UserProfile | null>(null);

  readonly isLoggedIn = computed(() => !!this.token());
  readonly roles = computed(() => rolesFromAccessToken(this.token()));
  readonly isAdmin = computed(() => this.roles().includes('ROLE_ADMIN'));

  constructor(
    private readonly http: HttpClient,
    private readonly router: Router
  ) {
    const existing = sessionStorage.getItem(this.storageKey);
    if (existing) {
      this.token.set(existing);
      this.refreshProfile().subscribe({ error: () => this.logout() });
    }
  }

  api(path: string): string {
    const base = environment.apiUrl.replace(/\/$/, '');
    const p = path.startsWith('/') ? path : `/${path}`;
    return base ? `${base}${p}` : p;
  }

  login(email: string, password: string) {
    return this.http
      .post<LoginResponse>(this.api('/api/auth/login'), { email, password })
      .pipe(
        tap((res) => {
          sessionStorage.setItem(this.storageKey, res.accessToken);
          this.token.set(res.accessToken);
        })
      );
  }

  register(body: {
    email: string;
    password: string;
    firstName: string;
    lastName: string;
  }) {
    return this.http.post<UserProfile>(this.api('/api/auth/register'), body).pipe(
      tap(() => {
        /* user registers — then login separately */
      })
    );
  }

  refreshProfile() {
    return this.http.get<UserProfile>(this.api('/api/auth/me')).pipe(
      tap((p) => {
        this.profile.set(p);
      })
    );
  }

  logout() {
    sessionStorage.removeItem(this.storageKey);
    this.token.set(null);
    this.profile.set(null);
    this.router.navigate(['/']);
  }
}
