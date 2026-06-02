import { inject } from "@angular/core"
import { CookieCustomService } from "../services/cookie.custom.service"
import { Router } from "@angular/router"
import { Roles } from "../role/roles"

export const roleAdminGuard = () => {
    const cookieService: CookieCustomService = inject(CookieCustomService)
    const router: Router = inject(Router)

    const roleId = cookieService.getCookie('roleId');

    return roleId === Roles.ADMIN.valueOf().toString() ? true : router.navigate(['/'])
}
