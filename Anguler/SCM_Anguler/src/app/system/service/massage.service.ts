import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environment/environment';
import { MessageRequestModel, MessageResponseModel } from '../massageModel';


@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private apiUrl = environment.apiUrl + 'messages';

  constructor(private http: HttpClient) { }

  /**
   * 💡 কাস্টম হেডার বাদ দেওয়া হয়েছে। 
   * আপনার প্রজেক্টের AuthInterceptor স্বয়ংক্রিয়ভাবে Bearer Token ব্যাকএন্ডে ম্যাপ করবে।
   */
  getInbox(): Observable<MessageResponseModel[]> {
    return this.http.get<MessageResponseModel[]>(`${this.apiUrl}/inbox`);
  }

  send(message: MessageRequestModel): Observable<MessageResponseModel[]> {
    return this.http.post<MessageResponseModel[]>(this.apiUrl, message);
  }

  markAsRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/read`, {});
  }
}
