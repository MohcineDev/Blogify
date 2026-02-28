import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Route, Router } from '@angular/router';
import { catchError, throwError, EMPTY } from 'rxjs';
//HttpClient supports a form of middleware known as interceptors.
//This interceptor will automatically attach your JWT token to every outgoing HTTP request, so you won’t need to manually include it each time you call protected backend endpoints.
// The interceptor can modify the request (for example, to add headers).

// Then it calls next.handle() to send the request onward.
export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const token = localStorage.getItem("token")
  let headers = req.headers;

  if (token) {
    headers = headers.set('Authorization', `Bearer ${token}`)
    req = req.clone({
      headers
    });
  }

  const router = inject(Router);

  return next(req).pipe(
    catchError((err: HttpErrorResponse) => {
      const status = err.status
      
      if (err.status == 404) {
        router.navigate(['/404'])
        return EMPTY
      }
       if (status === 403) {

       
        router.navigate(['/forbiden'])
      }

      ///unauthorized  - expired token
      //not in login page
      if (status === 401 && !router.url.includes('login')) {
        toLogin(router)
        return EMPTY
      }


      // re-throw so the component that made the request will handle it
      return throwError(() => err)

    })
  )
}

const toLogin = (router: Router) => {
  localStorage.removeItem('token')
  router.navigate(['/login'])
  return EMPTY
}