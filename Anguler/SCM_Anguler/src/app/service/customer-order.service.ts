import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../encironment/environment';
import { CustomerOrderRequestModel, CustomerOrderResponseModel } from '../component/shared/model/customerOrder';

@Injectable({
  providedIn: 'root',
})
export class CustomerOrderService {
  private apiUrl = environment.apiUrl + "customerOrders";

  constructor(private http: HttpClient) { }

  findAll(): Observable<CustomerOrderResponseModel[]> {
    return this.http.get<CustomerOrderResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<CustomerOrderResponseModel> {
    return this.http.get<CustomerOrderResponseModel>(`${this.apiUrl}/${id}`);
  }

  save(order: CustomerOrderRequestModel): Observable<CustomerOrderResponseModel> {
    return this.http.post<CustomerOrderResponseModel>(this.apiUrl, order);
  }

  update(id: number, order: CustomerOrderRequestModel): Observable<CustomerOrderResponseModel> {
    return this.http.put<CustomerOrderResponseModel>(`${this.apiUrl}/${id}`, order);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }

  trackOrder(orderNumber: string): Observable<CustomerOrderResponseModel> {
    return this.http.get<CustomerOrderResponseModel>(`${this.apiUrl}/track`, {
      params: { orderNumber: orderNumber }
    });
  }

  verifyPaymentLink(orderId: number, amountPaid: number, method: string): Observable<string> {
    return this.http.get(`${this.apiUrl}/verify-link`, {
      params: { orderId: orderId, amountPaid: amountPaid, method: method },
      responseType: 'text'
    });
  }
}