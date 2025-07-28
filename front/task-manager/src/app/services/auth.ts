import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import {
  catchError,
  map,
  Observable,
  Subscription,
  throwError,
  timer,
} from 'rxjs';

interface LoginResponse {
  status: string;
  type: string;
  token: string;
  userId: string;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private tokenKey = 'token';
  private credsKey = 'creds';
  private API_URL = 'http://localhost:8080';

  private tokenCheckSubscription?: Subscription;

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>(`${this.API_URL}/auth/login`, { email, password })
      .pipe(
        map((res) => {
          if (res.status === 'Sucesso' && res.token) {
            this.saveToken(res.token);
            this.saveCreds(email, password);
            localStorage.setItem('userId', res.userId);
            this.startTokenValidationTimer();
            return res;
          } else {
            throw new Error('Login falhou: resposta inválida');
          }
        }),
        catchError((err) => {
          let errorMessage = 'Erro ao realizar login';
          if (err.error?.description && Array.isArray(err.error.description)) {
            const firstMsg = err.error.description[0];
            if (firstMsg?.message) {
              errorMessage = firstMsg.message;
            }
          } else if (err.error?.message) {
            errorMessage = err.error.message;
          }
          return throwError(() => errorMessage);
        })
      );
  }

  private saveToken(token: string) {
    if (typeof document !== 'undefined') {
      document.cookie = `${this.tokenKey}=${token}; path=/; secure; SameSite=Strict`;
    }
  }

  private getToken(): string | null {
    if (typeof document === 'undefined') return null;
    return this.getCookie(this.tokenKey);
  }

  private getCookie(name: string): string | null {
    if (typeof document === 'undefined') return null;
    const nameEQ = name + '=';
    const ca = document.cookie.split(';');
    for (let c of ca) {
      c = c.trim();
      if (c.indexOf(nameEQ) === 0) return c.substring(nameEQ.length);
    }
    return null;
  }

  private saveCreds(email: string, password: string) {
    const creds = JSON.stringify({ email, password });
    localStorage.setItem(this.credsKey, creds);
  }

  private getCreds(): { email: string; password: string } | null {
    const creds = localStorage.getItem(this.credsKey);
    return creds ? JSON.parse(creds) : null;
  }

  logout() {
    this.clearBrowserData();

    if (this.tokenCheckSubscription) {
      this.tokenCheckSubscription.unsubscribe();
    }

    this.router.navigate(['/login']);
  }

  clearBrowserData(): void {
    if (typeof window !== 'undefined' && typeof document !== 'undefined') {

      const cookies = document.cookie.split(';');
      for (let cookie of cookies) {
        const eqPos = cookie.indexOf('=');
        const name =
          eqPos > -1 ? cookie.substring(0, eqPos).trim() : cookie.trim();
        document.cookie = `${name}=; Max-Age=0; path=/; secure; SameSite=Strict`;
      }
    }
  }

  startTokenValidationTimer() {
    this.tokenCheckSubscription?.unsubscribe();

    this.tokenCheckSubscription = timer(0, 60 * 1000).subscribe(() => {
      const token = this.getToken();
      if (!token) {
        this.logout();
        return;
      }

      const tokenExp = this.getTokenExpirationDate(token);
      if (!tokenExp) {
        this.logout();
        return;
      }

      const now = new Date();
      const diffMs = tokenExp.getTime() - now.getTime();
      const diffMinutes = diffMs / 1000 / 60;

      if (diffMinutes <= 10 && diffMinutes > 0) {
        this.askRenewToken();
      } else if (diffMinutes <= 0) {
        this.logout();
      }
    });
  }

  private getTokenExpirationDate(token: string): Date | null {
    try {
      const payloadBase64 = token.split('.')[1];
      const payloadJson = atob(payloadBase64);
      const payload = JSON.parse(payloadJson);
      return payload.exp ? new Date(payload.exp * 1000) : null;
    } catch {
      return null;
    }
  }

  private askRenewToken() {
    const stayConnected = window.confirm(
      'Sua sessão vai expirar em breve. Deseja permanecer conectado?'
    );
    if (stayConnected) {
      this.renewToken();
    } else {
      this.logout();
    }
  }

  private renewToken() {
    const creds = this.getCreds();
    if (!creds) {
      this.logout();
      return;
    }

    this.login(creds.email, creds.password).subscribe({
      next: () => console.log('Token renovado com sucesso!'),
      error: () => this.logout(),
    });
  }

  register(data: {
    nome: string;
    email: string;
    password: string;
    documento: string;
    endereco: string;
  }): Observable<any> {
    return this.http.post(`${this.API_URL}/auth/cadastrar`, data);
  }
}
