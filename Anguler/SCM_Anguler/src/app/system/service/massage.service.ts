import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { StorageService } from '../../auth/auth_service/storage.service';
import { environment } from '../../../environment/environment';
import { MessageRequestModel, MessageResponseModel } from '../massageModel';

@Injectable({
  providedIn: 'root',
})
export class MessageService {
  private apiUrl = environment.apiUrl + 'messages';

  constructor(
    private http: HttpClient,
    private storage: StorageService,
  ) {}

  private getHeaders(): HttpHeaders {
    const userId = this.storage.getUser()?.userId?.toString() ?? '';
    return new HttpHeaders().set('X-User-Id', userId);
  }

  getInbox(): Observable<MessageResponseModel[]> {
    return this.http.get<MessageResponseModel[]>(`${this.apiUrl}/inbox`, {
      headers: this.getHeaders(),
    });
  }

  send(message: MessageRequestModel): Observable<MessageResponseModel[]> {
    return this.http.post<MessageResponseModel[]>(this.apiUrl, message, {
      headers: this.getHeaders(),
    });
  }

  markAsRead(id: number): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/${id}/read`, {}, { headers: this.getHeaders() });
  }
}
