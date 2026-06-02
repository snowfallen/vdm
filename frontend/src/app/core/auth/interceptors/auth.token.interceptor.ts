import { inject } from "@angular/core"
import { HttpInterceptorFn } from "@angular/common/http"
import {CookieCustomService} from '../../services/cookie.custom.service';

export const authTokenInterceptor: HttpInterceptorFn = (req, next) => {
    const cookieService: CookieCustomService = inject(CookieCustomService)
    const token = cookieService.getCookie('jwt');
    if (token) {
        req = req.clone({
            setHeaders: {
                Authorization: `Bearer ${token}`
            }
        })
    }

    return next(req)
}
