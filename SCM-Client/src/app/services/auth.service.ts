import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private authUrl = environment.apiUrl + 'auth/';
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  login(credentials: any): Observable<any> {
    return this.http.post(this.authUrl + 'login', credentials);
  }

  verifyEmail(token: string): Observable<string> {
    return this.http.get(this.authUrl + 'verify-email', {
      params: { token },
      responseType: 'text'
    });
  }

  forgotPassword(emailDto: any): Observable<string> {
    return this.http.post(this.authUrl + 'forgot-password', emailDto, {
      responseType: 'text'
    });
  }

  resetPassword(resetDto: any): Observable<string> {
    return this.http.post(this.authUrl + 'reset-password', resetDto, {
      responseType: 'text'
    });
  }

  registerCustomer(formData: FormData): Observable<any> {
    return this.http.post(this.apiUrl + 'customers', formData);
  }

  registerSupplier(formData: FormData): Observable<any> {
    return this.http.post(this.apiUrl + 'suppliers', formData);
  }

  registerDriver(formData: FormData): Observable<any> {
    return this.http.post(this.apiUrl + 'drivers', formData);
  }
}
