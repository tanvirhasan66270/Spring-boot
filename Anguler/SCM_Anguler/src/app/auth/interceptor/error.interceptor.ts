import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { StorageService } from '../auth_service/storage.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const storage = inject(StorageService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        storage.clearSession();
        router.navigate(['/login']);
      } else if (error.status === 403) {
        router.navigate(['/dashboard']);
      }
      return throwError(() => error);
    }),
  );
};
