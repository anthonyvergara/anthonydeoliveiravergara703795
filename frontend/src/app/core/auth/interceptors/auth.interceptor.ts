import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthFacade } from '../facade/auth.facade';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authFacade = inject(AuthFacade);

  if (req.url.includes('/auth/login') || req.url.includes('/auth/refresh')) {
    return next(req);
  }

  const accessToken = authFacade.getAccessToken();
  if (accessToken) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${accessToken}`
      }
    });
  }

  return next(req).pipe(
    catchError(error => {
      if ((error.status === 401 || error.status === 403) && !req.url.includes('/auth/refresh')) {
        return authFacade.refreshToken().pipe(
          switchMap(() => {
            const newToken = authFacade.getAccessToken();
            const retryReq = req.clone({
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });
            return next(retryReq);
          }),
          catchError(refreshError => {
            authFacade.logout();
            return throwError(() => refreshError);
          })
        );
      }
      return throwError(() => error);
    })
  );
};

