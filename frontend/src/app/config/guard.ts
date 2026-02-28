import { Token } from "../services/token";
import { inject, untracked } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivateFn, Router } from "@angular/router";

export const authGuard: CanActivateFn = (route: ActivatedRouteSnapshot) => {
    const tokenService = inject(Token)
    const router = inject(Router)
    const url = route.routeConfig?.path

    const token = tokenService.getToken()
    if (!token) {
        if (url === 'login' || url === 'register') {
            return true;
        }
        router.navigate(['/login'])

        return false;
    }

    if (tokenService.getExpired()) {
        tokenService.logout()
        router.navigate(['/login'])
        return false
    }

    //admin to dashboard
    //user -> home
    const role = tokenService.getRole()

    if (url === 'dashboard' && role !== 'ADMIN') {
        router.navigate(['/home'])
       return false
    }
    //  else 
    //     if (url === 'home' && role !== 'USER') {
    //     router.navigate(['/dashboard'])
    //     return false
    // }

    ///can 't go back to login or register
    if (!tokenService.getExpired() && (url === 'login' || url === 'register')) {
        router.navigate(['/home'])
        // router.navigate([role === 'ADMIN' ? '/dashboard' : '/home'])
        return false
    }


    return true
}
