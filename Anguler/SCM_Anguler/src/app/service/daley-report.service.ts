
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { DailyReportResponseModel } from '../component/shared/model/daley-report';

@Injectable({
  providedIn: 'root',
})
export class DailyReportService {
  // ব্যাকএন্ডের রুট ম্যাপিংয়ের সাথে পারফেক্ট সিঙ্ক
  private apiUrl = environment.apiUrl + 'reports/'; 

  constructor(private http: HttpClient) { }

  findAll(): Observable<DailyReportResponseModel[]> {
    return this.http.get<DailyReportResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<DailyReportResponseModel> {
    return this.http.get<DailyReportResponseModel>(`${this.apiUrl}${id}`);
  }

  getByWarehouse(warehouseId: string): Observable<DailyReportResponseModel[]> {
    return this.http.get<DailyReportResponseModel[]>(`${this.apiUrl}warehouse/${warehouseId}`);
  }

  create(formData: FormData): Observable<DailyReportResponseModel> {
    return this.http.post<DailyReportResponseModel>(this.apiUrl, formData);
  }

  update(id: number, formData: FormData): Observable<DailyReportResponseModel> {
    return this.http.put<DailyReportResponseModel>(`${this.apiUrl}${id}`, formData);
  }

  approve(id: number): Observable<DailyReportResponseModel> {
    return this.http.patch<DailyReportResponseModel>(`${this.apiUrl}approve/${id}`, {});
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}${id}`);
  }
}