import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { LoginRequestDto } from '../dtos/login-request.dto';
import { TokenResponseDto } from '../dtos/token-response.dto';
import { RefreshRequestDto } from '../dtos/refresh-request.dto';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = environment.apiUrl;

  constructor(private http: HttpClient) {}

  login(credentials: LoginRequestDto): Observable<TokenResponseDto> {
    return this.http.post<TokenResponseDto>(`${this.API_URL}/auth/login`, credentials);
  }

  refresh(refreshToken: string): Observable<TokenResponseDto> {
    const request: RefreshRequestDto = { refreshToken };
    return this.http.post<TokenResponseDto>(`${this.API_URL}/auth/refresh`, request);
  }
}

