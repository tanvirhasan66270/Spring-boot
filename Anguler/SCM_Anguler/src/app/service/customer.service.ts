import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../encironment/environment';
import { CustomerRequestModel, CustomerResponseModel } from '../component/shared/model/customerModel';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = environment.apiUrl + "customer/";

  constructor(private http: HttpClient) { }

  getAll(): Observable<CustomerResponseModel[]> {
    return this.http.get<CustomerResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<CustomerResponseModel> {
    return this.http.get<CustomerResponseModel>(`${this.apiUrl}/${id}`);
  }

  save(customer: CustomerRequestModel, imageFile: File | null): Observable<CustomerResponseModel> {
    const formData = new FormData();
    
    formData.append(
      "customer",
      new Blob([JSON.stringify(customer)], { type: "application/json" })
    );

    if (imageFile) {
      formData.append("image", imageFile);
    }

    return this.http.post<CustomerResponseModel>(this.apiUrl, formData);
  }

  update(id: number, customer: CustomerRequestModel, imageFile: File | null): Observable<CustomerResponseModel> {
    const formData = new FormData();
    
    formData.append( "customer",
      new Blob([JSON.stringify(customer)], { type: "application/json" })
    );

    if (imageFile) {
      formData.append("image", imageFile);
    }

    return this.http.put<CustomerResponseModel>(`${this.apiUrl}${id}`, formData);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}${id}`, { responseType: 'text' });
  }
}