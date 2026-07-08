import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../encironment/environment';
import { PurchaseOrderRequestModel, PurchaseOrderResponseModel } from '../component/shared/model/purchaseOrderModel';

@Injectable({
  providedIn: 'root',
})
export class PurchaseOrderService {
  private apiUrl = environment.apiUrl + "purchase-orders";

  constructor(private http: HttpClient) { }

  findAll(): Observable<PurchaseOrderResponseModel[]> {
    return this.http.get<PurchaseOrderResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<PurchaseOrderResponseModel> {
    return this.http.get<PurchaseOrderResponseModel>(`${this.apiUrl}/${id}`);
  }

  save(order: PurchaseOrderRequestModel): Observable<PurchaseOrderResponseModel> {
    return this.http.post<PurchaseOrderResponseModel>(this.apiUrl, order);
  }

  update(id: number, order: PurchaseOrderRequestModel): Observable<PurchaseOrderResponseModel> {
    return this.http.put<PurchaseOrderResponseModel>(`${this.apiUrl}/${id}`, order);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}