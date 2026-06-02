import {HttpClient, HttpParams} from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { CookieCustomService } from '../../services/cookie.custom.service';
import { Router } from '@angular/router';
import { catchError, map, Observable, of } from 'rxjs';
import {environment} from "../../../../environments/environment";
import {IToken} from '../models/i-token';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private static readonly TOKEN_NAME: string = 'jwt';
  private static readonly BASE_URL: string = environment.rootUrl + 'auth/';
  private static readonly ROLE_ID: string = 'roleId';
  private readonly http: HttpClient = inject(HttpClient);
  private readonly cookieService: CookieCustomService = inject(CookieCustomService);
  private readonly router: Router = inject(Router);

  public login(credentials: { email: string; password: string }): Observable<number> {
    return this.http.post<IToken>(`${AuthService.BASE_URL}login`, credentials).pipe(
      map(response => {
        // Set cookies with the root path
        this.cookieService.setCookie(AuthService.TOKEN_NAME, response.token, '/');
        this.cookieService.setCookie(AuthService.ROLE_ID, response.roleId.toString(), '/');
        this.router.navigate(['/']);
        return 200;
      }),
      catchError(error => {
        return of(error.status);
      })
    );
  }

  public logout(): boolean {
    if (this.cookieService.getCookie(AuthService.TOKEN_NAME)) {
      this.clearCookies('/');
      this.router.navigate(['/']);

      return true;
    }
    return false;
  }

  public clearCookies(path: string = '/'): void {
    this.cookieService.deleteCookies(path);
  }

  /** ⚡ weryfikacja linku */
  verifyEmail(token: string): Observable<string> {
    return this.http.get(`${AuthService.BASE_URL}verify`, {
      params: new HttpParams().set('token', token),
      responseType: 'text'          // ← очікуємо plain-text
    });
  }

  /** ⚡ ponowne wysłanie linku */
  resendVerification(email: string): Observable<string> {
    return this.http.post(`${AuthService.BASE_URL}resend-verification`, null, {
      params: new HttpParams().set('email', email),
      responseType: 'text'          // ← також plain-text
    });
  }

  public getRoleId(): number {
    const roleId = this.cookieService.getCookie(AuthService.ROLE_ID);
    return roleId ? Number(roleId) : 0;
  }
}
