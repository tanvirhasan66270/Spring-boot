import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { QuoteRequestModel } from '../component/shared/model/quote_requestModel';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class QuoteService {

  private apiUrl = 'http://localhost:8085/api/public/quotes/submit';

  constructor(private http: HttpClient) {}

  submitRequest(data: QuoteRequestModel): Observable<QuoteRequestModel> {
    return this.http.post<QuoteRequestModel>(this.apiUrl, data);
  }
}
