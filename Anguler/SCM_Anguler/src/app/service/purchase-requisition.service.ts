import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../encironment/environment';
import { purchaseRequisitionRequestModel, purchaseRequisitionResponseModel } from '../component/shared/model/purchase-requisionModel';

@Injectable({
  providedIn: 'root',
})
export class PurchaseRequisitionService {
  private apiUrl = environment.apiUrl + "purchase-requisitions";

  constructor(private http: HttpClient) { }

  findAll(): Observable<purchaseRequisitionResponseModel[]> {
    return this.http.get<purchaseRequisitionResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<purchaseRequisitionResponseModel> {
    return this.http.get<purchaseRequisitionResponseModel>(`${this.apiUrl}/${id}`);
  }

  save(requisition: purchaseRequisitionRequestModel): Observable<purchaseRequisitionResponseModel> {
    return this.http.post<purchaseRequisitionResponseModel>(this.apiUrl, requisition);
  }

  update(id: number, requisition: purchaseRequisitionRequestModel): Observable<purchaseRequisitionResponseModel> {
    return this.http.put<purchaseRequisitionResponseModel>(`${this.apiUrl}/${id}`, requisition);
  }

  approve(id: number): Observable<purchaseRequisitionResponseModel> {
    return this.http.put<purchaseRequisitionResponseModel>(`${this.apiUrl}/${id}/approve`, {});
  }

  rejectOrCancel(id: number, actionType: 'REJECT' | 'CANCEL'): Observable<purchaseRequisitionResponseModel> {
    return this.http.put<purchaseRequisitionResponseModel>(`${this.apiUrl}/${id}/reject-or-cancel`, {}, {
      params: { actionType: actionType }
    });
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
}