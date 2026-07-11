import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { NotificationModel } from '../NotificationModel';


@Injectable({
  providedIn: 'root',
})
export class NotificationService {
  private apiUrl = environment.apiUrl + 'notifications';

  constructor(private http: HttpClient) { }

  // 💡 সিকিউরড আর্কিটেকচার ইন্টিগ্রেশনের জন্য হেডার মেথড
  private getHeaders(): HttpHeaders {
    return new HttpHeaders().set('X-User-Id', '16'); // আপনার প্রজেক্টের কারেন্ট মক/লগড-ইন ইউজার আইডি
  }

  /**
   * 🔍 কারেন্ট ইউজারের সমস্ত নোটিফিকেশন লিস্ট ফেচ করা
   */
  findAll(): Observable<NotificationModel[]> {
    return this.http.get<NotificationModel[]>(this.apiUrl, { headers: this.getHeaders() });
  }

  /**
   * 📊 ড্যাশবোর্ড বেল আইকনের জন্য কারেন্ট আনরিড কাউন্ট আনা
   */
  getUnreadCount(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/unread-count`, { headers: this.getHeaders() });
  }

  /**
   * 🔄 নির্দিষ্ট কোনো নোটিফিকেশন রিড (Read) হিসেবে স্ট্যাম্প করা
   */
  markAsRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/read`, {}, { headers: this.getHeaders() });
  }

  /**
   * 🧹 এক ক্লিকে ইনবক্সের সমস্ত আনরিড নোটিফিকেশন ক্লিয়ার করা
   */
  markAllAsRead(): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/read-all`, {}, { headers: this.getHeaders() });
  }
}
