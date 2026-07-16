import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { PurchaseOrderRequestModel, PurchaseOrderResponseModel } from '../component/shared/model/purchaseOrderModel';
import { StorageService } from '../auth/auth_service/storage.service'; 

@Injectable({
  providedIn: 'root',
})
export class PurchaseOrderService {
  private apiUrl = environment.apiUrl + "purchase-orders";

  constructor(
    private http: HttpClient,
    private storage: StorageService 
  ) { }

  /**
   * 🛡️ অথেন্টিকেশন হেডার জেনারেট করা
   */
  private getAuthHeaders(): HttpHeaders {
    const token = this.storage.getToken() ?? '';
    return new HttpHeaders().set('Authorization', `Bearer ${token}`);
  }

  /**
   * 🔍 সমস্ত পারচেজ অর্ডার ফেচ করা
   */
  findAll(): Observable<PurchaseOrderResponseModel[]> {
    return this.http.get<PurchaseOrderResponseModel[]>(this.apiUrl, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * 🆔 নির্দিষ্ট আইডি দিয়ে পারচেজ অর্ডার খোঁজা
   */
  getById(id: number): Observable<PurchaseOrderResponseModel> {
    return this.http.get<PurchaseOrderResponseModel>(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * 💾 নতুন পারচেজ অর্ডার সেভ করা
   */
  save(order: PurchaseOrderRequestModel): Observable<PurchaseOrderResponseModel> {
    return this.http.post<PurchaseOrderResponseModel>(this.apiUrl, order, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * 🔄 এক্সিস্টিং পারচেজ অর্ডার আপডেট করা
   */
  update(id: number, order: PurchaseOrderRequestModel): Observable<PurchaseOrderResponseModel> {
    return this.http.put<PurchaseOrderResponseModel>(`${this.apiUrl}/${id}`, order, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * ✅ পারচেজ অর্ডার অ্যাপ্রুভ করা
   */
  approve(id: number): Observable<PurchaseOrderResponseModel> {
    return this.http.put<PurchaseOrderResponseModel>(`${this.apiUrl}/${id}/approve`, {}, {
      headers: this.getAuthHeaders()
    });
  }

  /**
   * 🚀 সরাসরি স্ট্যাটাস পরিবর্তন করা (RECEIVED বা CANCELLED)
   */
  changeStatus(id: number, status: 'RECEIVED' | 'CANCELLED'): Observable<PurchaseOrderResponseModel> {
    return this.http.put<PurchaseOrderResponseModel>(
      `${this.apiUrl}/${id}/status`, 
      {}, 
      { 
        headers: this.getAuthHeaders(), // ইন্টারসেপ্টর ব্যাকআপ হিসেবে হেডার যুক্ত রাখা সেফ
        params: { status: status } 
      }
    );
  }

  /**
   * ❌ পারচেজ অর্ডার ডিলিট করা
   */
  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, {
      headers: this.getAuthHeaders(),
      responseType: 'text'
    });
  }

  /**
   * 🎯 নির্দিষ্ট সাপ্লায়ারের সমস্ত পারচেজ অর্ডার হিস্ট্রি নিয়ে আসা
   */
  getOrdersBySupplierId(supplierId: number): Observable<PurchaseOrderResponseModel[]> {
    return this.http.get<PurchaseOrderResponseModel[]>(`${this.apiUrl}/supplier/${supplierId}`, {
      headers: this.getAuthHeaders()
    });
  }
}