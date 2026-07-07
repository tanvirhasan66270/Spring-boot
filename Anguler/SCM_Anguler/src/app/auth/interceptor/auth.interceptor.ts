import { HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { StorageService } from "../auth_service/storage.service";



export const authInterceptor: HttpInterceptorFn = (req, next) => {
    
  const storage = inject(StorageService);
  const token   = storage.getToken();

  if (token) {
    req = req.clone({
      setHeaders: { Authorization: `Bearer ${token}` }
    });
  }

  return next(req);
};