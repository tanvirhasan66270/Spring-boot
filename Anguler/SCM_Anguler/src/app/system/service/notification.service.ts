import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { NotificationModel } from '../NotificationModel';
import { StorageService } from '../../auth/auth_service/storage.service';

@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private apiUrl = environment.apiUrl + 'notifications';

  constructor(
    private http: HttpClient,
    private storage: StorageService,
  ) {}

  private getHeaders(): HttpHeaders {
    const userId = this.storage.getUser()?.userId?.toString() ?? '1';
    return new HttpHeaders().set('X-User-Id', userId);
  }


  findAll(): Observable<NotificationModel[]> {
    return this.http.get<NotificationModel[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  
  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`, { headers: this.getHeaders() });
  }

 
  markAsRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/read`, {}, { headers: this.getHeaders() });
  }

 
  markAllAsRead(): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/read-all`, {}, { headers: this.getHeaders() });
  }
}
