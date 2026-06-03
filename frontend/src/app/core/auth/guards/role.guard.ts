import { inject } from "@angular/core"
import { Router } from "@angular/router"
import {CookieCustomService} from '../../services/cookie.custom.service';
import {Roles} from '../enums/roles';

export const roleAdminGuard = () => {
    const cookieService: CookieCustomService = inject(CookieCustomService)
    const router: Router = inject(Router)

    const roleId = cookieService.getCookie('roleId');

    return roleId === Roles.ADMIN.valueOf().toString() ? true : router.navigate(['/'])
}

export const roleClientGuard = () => {
  const cookieService: CookieCustomService = inject(CookieCustomService)
  const router: Router = inject(Router)

  const roleId = cookieService.getCookie('roleId');

  return roleId === Roles.CLIENT.valueOf().toString() ? true : router.navigate(['/'])
}
