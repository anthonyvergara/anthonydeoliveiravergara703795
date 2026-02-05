import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, tap, catchError, throwError, map } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { AuthStateService } from '../state/auth.state';
import { LoginRequestDto } from '../dtos/login-request.dto';
import { AuthUser, AuthTokens } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthFacade {
  constructor(
    private authService: AuthService,
    private authState: AuthStateService,
    private router: Router
  ) {}

  login(credentials: LoginRequestDto): Observable<void> {
    return this.authService.login(credentials).pipe(
      tap(response => {
        const user: AuthUser = {
          username: response.username,
          role: response.role
        };
        const tokens: AuthTokens = {
          accessToken: response.accessToken,
          refreshToken: response.refreshToken
        };
        this.authState.setAuthenticated(user, tokens);
      }),
      tap(() => this.router.navigate(['/home'])),
      catchError(error => {
        console.error('Login failed:', error);
        return throwError(() => error);
      }),
      map(() => void 0)
    );
  }

  logout(): void {
    this.authState.clearAuthentication();
    this.router.navigate(['/login']);
  }

  refreshToken(): Observable<void> {
    const refreshToken = this.authState.getRefreshToken();
    if (!refreshToken) {
      this.logout();
      return throwError(() => new Error('No refresh token available'));
    }

    return this.authService.refresh(refreshToken).pipe(
      tap(response => {
        const user: AuthUser = {
          username: response.username,
          role: response.role
        };
        const tokens: AuthTokens = {
          accessToken: response.accessToken,
          refreshToken: response.refreshToken
        };
        this.authState.setAuthenticated(user, tokens);
      }),
      catchError(error => {
        console.error('Token refresh failed:', error);
        this.logout();
        return throwError(() => error);
      }),
      map(() => void 0)
    );
  }

  isAuthenticated(): boolean {
    return this.authState.isAuthenticated();
  }

  getAccessToken(): string | null {
    return this.authState.getAccessToken();
  }

  getUser(): AuthUser | null {
    return this.authState.getUser();
  }

  getAuthState$(): Observable<any> {
    return this.authState.getState$();
  }
}

