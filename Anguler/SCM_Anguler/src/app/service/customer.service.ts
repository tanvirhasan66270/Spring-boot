import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { CustomerResponseModel } from '../component/shared/model/customerModel';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = environment.apiUrl + "customer/";

  constructor(private http: HttpClient) {}

  getAll(): Observable<CustomerResponseModel[]> {
    return this.http.get<CustomerResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<CustomerResponseModel> {
    return this.http.get<CustomerResponseModel>(`${this.apiUrl}${id}`);
  }

  getCustomerByUserId(userId: number): Observable<CustomerResponseModel> {
    return this.http.get<CustomerResponseModel>(`${this.apiUrl}user/${userId}`);
  }

  // Multi-part FormData হ্যান্ডলিং
  save(formData: FormData): Observable<CustomerResponseModel> {
    return this.http.post<CustomerResponseModel>(this.apiUrl, formData);
  }

  update(id: number, formData: FormData): Observable<CustomerResponseModel> {
    return this.http.put<CustomerResponseModel>(`${this.apiUrl}${id}`, formData);
  }

  delete(id: number): Observable<string> {
    // কন্ট্রোলারের Plain text রেসপন্সের কারণে responseType: 'text' বাধ্যতামূলক
    return this.http.delete(`${this.apiUrl}${id}`, { responseType: 'text' });
  }
}