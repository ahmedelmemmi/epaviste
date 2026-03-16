import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = environment.apiUrl;
  private currentUserSubject = new BehaviorSubject<AuthResponse | null>(null);
  currentUser$ = this.currentUserSubject.asObservable();

  constructor(private http: HttpClient) {
    const stored = localStorage.getItem('auth');
    if (stored) this.currentUserSubject.next(JSON.parse(stored));
  }

  login(request: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/login`, request).pipe(
      tap(response => {
        localStorage.setItem('auth', JSON.stringify(response));
        this.currentUserSubject.next(response);
      })
    );
  }

  register(request: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/auth/register`, request).pipe(
      tap(response => {
        localStorage.setItem('auth', JSON.stringify(response));
        this.currentUserSubject.next(response);
      })
    );
  }

  logout(): void {
    localStorage.removeItem('auth');
    this.currentUserSubject.next(null);
  }

  get currentUser(): AuthResponse | null {
    return this.currentUserSubject.value;
  }

  get token(): string | null {
    return this.currentUser?.token || null;
  }

  get isLoggedIn(): boolean {
    return !!this.token;
  }

  get userRole(): string {
    return this.currentUser?.role || '';
  }

  isBuyer(): boolean { return this.userRole === 'BUYER'; }
  isSeller(): boolean { return this.userRole === 'SELLER'; }
  isAdmin(): boolean { return this.userRole === 'ADMIN'; }
}
