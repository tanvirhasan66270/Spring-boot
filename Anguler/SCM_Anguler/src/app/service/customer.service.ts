import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';

@Injectable({
  providedIn: 'root',
})
export class CustomerService {
  private apiUrl = environment.apiUrl + "customer/";

  constructor(private http: HttpClient) { }

  getAll(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }

  getById(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}`);
  }

  save(customer: any, imageFile: File | null): Observable<any> {
    const formData = new FormData();
    
    formData.append(
      "customer",
      new Blob([JSON.stringify(customer)], { type: "application/json" })
    );

    if (imageFile) {
      formData.append("image", imageFile);
    }

    return this.http.post<any>(this.apiUrl, formData);
  }

  update(id: number, customer: any, imageFile: File | null): Observable<any> {
    const formData = new FormData();
    
    formData.append( "customer",
      new Blob([JSON.stringify(customer)], { type: "application/json" })
    );

    if (imageFile) {
      formData.append("image", imageFile);
    }

    return this.http.put<any>(`${this.apiUrl}${id}`, formData);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}${id}`, { responseType: 'text' });
  }
}
