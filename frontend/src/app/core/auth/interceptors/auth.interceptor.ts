import {
  HttpErrorResponse,
  HttpInterceptorFn
} from "@angular/common/http";
import {Router} from "@angular/router";
import {catchError, Observable, throwError} from "rxjs";
import {inject} from "@angular/core";
import {MatSnackBar} from "@angular/material/snack-bar";
import {CookieCustomService} from '../../services/cookie.custom.service';

export const authInterceptor: HttpInterceptorFn = (req , next) => {
  const router = inject(Router);
  const cookieService = inject(CookieCustomService);
  const snackBar: MatSnackBar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        cookieService.deleteCookies('/');
        router.navigate(['/login']);
        snackBar.open('Twoja sesja wygasła, proszę zaloguj się ponownie.', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'top',
          panelClass: ['error-snackbar']
        });
      }
      return throwError(error);
    })
  );
};
