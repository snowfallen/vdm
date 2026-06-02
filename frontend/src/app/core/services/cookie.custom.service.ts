import { inject, Injectable } from '@angular/core';
import { CookieService } from 'ngx-cookie-service';

@Injectable({
  providedIn: 'root'
})
export class CookieCustomService {
  private readonly cookieService: CookieService = inject(CookieService);

  public setCookie(cookieName: string, cookieValue: string, path: string = '/'): void {
    this.cookieService.set(cookieName, cookieValue, undefined, path);
  }

  public getCookie(cookieName: string): string {
    return this.cookieService.get(cookieName);
  }

  public deleteCookie(cookieName: string, path: string = '/'): void {
    this.cookieService.delete(cookieName, path);
  }

  public deleteCookies(path: string = '/'): void {
    this.cookieService.deleteAll(path);
  }
}
