import { inject } from "@angular/core"
import { Router } from "@angular/router"
import {CookieCustomService} from '../../services/cookie.custom.service';

export const canActivateRoute = () => {
    const cookieService: CookieCustomService = inject(CookieCustomService)
    const router: Router = inject(Router)

    return cookieService.getCookie('jwt') ? true : router.navigate(['/login'])
}
