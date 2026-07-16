import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
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

  // 🚀 নতুন মেথড: ব্যাকএন্ডের DRAFT ভ্যালিডেশন লক বাইপাস করে সরাসরি স্ট্যাটাস পরিবর্তন করার জন্য
  changeStatus(id: number, status: 'RECEIVED' | 'CANCELLED'): Observable<PurchaseOrderResponseModel> {
    return this.http.put<PurchaseOrderResponseModel>(
      `${this.apiUrl}/${id}/status`, 
      {}, 
      { params: { status: status } }
    );
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }

  getOrdersBySupplierId(supplierId: number): Observable<PurchaseOrderResponseModel[]> {
  return this.http.get<PurchaseOrderResponseModel[]>(`${this.apiUrl}/supplier/${supplierId}`);
}
}