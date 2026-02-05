import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { AuthState, AuthTokens, AuthUser } from '../models/auth.model';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  private readonly STORAGE_KEY = 'auth_tokens';

  private initialState: AuthState = {
    user: null,
    tokens: null,
    isAuthenticated: false
  };

  private state$ = new BehaviorSubject<AuthState>(this.loadInitialState());

  getState$(): Observable<AuthState> {
    return this.state$.asObservable();
  }

  getState(): AuthState {
    return this.state$.getValue();
  }

  setAuthenticated(user: AuthUser, tokens: AuthTokens): void {
    const newState: AuthState = {
      user,
      tokens,
      isAuthenticated: true
    };
    this.state$.next(newState);
    this.saveTokens(tokens);
  }

  setTokens(tokens: AuthTokens): void {
    const currentState = this.getState();
    this.state$.next({
      ...currentState,
      tokens
    });
    this.saveTokens(tokens);
  }

  clearAuthentication(): void {
    this.state$.next(this.initialState);
    this.clearTokens();
  }

  isAuthenticated(): boolean {
    return this.getState().isAuthenticated;
  }

  getAccessToken(): string | null {
    return this.getState().tokens?.accessToken || null;
  }

  getRefreshToken(): string | null {
    return this.getState().tokens?.refreshToken || null;
  }

  getUser(): AuthUser | null {
    return this.getState().user;
  }

  private loadInitialState(): AuthState {
    const tokens = this.loadTokens();
    if (tokens) {
      return {
        user: null,
        tokens,
        isAuthenticated: true
      };
    }
    return this.initialState;
  }

  private saveTokens(tokens: AuthTokens): void {
    localStorage.setItem(this.STORAGE_KEY, JSON.stringify(tokens));
  }

  private loadTokens(): AuthTokens | null {
    const stored = localStorage.getItem(this.STORAGE_KEY);
    return stored ? JSON.parse(stored) : null;
  }

  private clearTokens(): void {
    localStorage.removeItem(this.STORAGE_KEY);
  }
}

