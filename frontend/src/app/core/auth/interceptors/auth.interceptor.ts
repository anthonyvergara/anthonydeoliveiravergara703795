import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthFacade } from '../facade/auth.facade';
import { ToastService } from '../../../shared/services/toast.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authFacade = inject(AuthFacade);
  const toastService = inject(ToastService);

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
      if (error.status === 429) {
        const retryAfter = error.error?.retryAfter || 60;
        const message = retryAfter >= 60
          ? 'Você fez muitas requisições. Aguarde 1 minuto.'
          : `Você fez muitas requisições. Aguarde ${retryAfter} segundos.`;

        toastService.showError(message, 5000);
        return throwError(() => error);
      }

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

