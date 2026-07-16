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

  /**
   * 🎯 ইউজার আইডির কাস্টম হেডার মেথড
   */
  private getHeaders(): HttpHeaders {
    const userId = this.storage.getUser()?.userId?.toString() ?? '';
    const token = this.storage.getToken() ?? '';
    
    return new HttpHeaders()
      .set('X-User-Id', userId)
      .set('Authorization', `Bearer ${token}`); // JWT এবং User ID দুটিই একসাথে সিঙ্ক করা হলো
  }


  findAll(): Observable<NotificationModel[]> {
    const headers = this.getHeaders();
    if (!headers.get('X-User-Id')) {
      console.warn('SCM Warning: Auth session initialized incomplete. Skipping findAll notifications.');
      return new Observable<NotificationModel[]>(obs => obs.next([]));
    }
    return this.http.get<NotificationModel[]>(this.apiUrl, { headers });
  }


  getUnreadCount(): Observable<number> {
    const headers = this.getHeaders();
    // 🎯 হেডার ভ্যালিডেশন সেফগার্ড (400 Bad Request আটকাতে)
    if (!headers.get('X-User-Id')) {
      console.warn('SCM Warning: User context missing. Aborting getUnreadCount pipeline.');
      return new Observable<number>(obs => obs.next(0));
    }
    return this.http.get<number>(`${this.apiUrl}/unread-count`, { headers });
  }


  markAsRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/read`, {}, { headers: this.getHeaders() });
  }


  markAllAsRead(): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/read-all`, {}, { headers: this.getHeaders() });
  }
}