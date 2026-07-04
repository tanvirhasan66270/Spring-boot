import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../encironment/environment';
import { DistrictRequestModel, DistrictResponseModel } from '../component/shared/model/districtModel';

@Injectable({
  providedIn: 'root',
})
export class DistrictService {
  private apiUrl = environment.apiUrl + "district";

  constructor(private http: HttpClient) { }

  getAll(): Observable<DistrictResponseModel[]> {
    return this.http.get<DistrictResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<DistrictResponseModel> {
    return this.http.get<DistrictResponseModel>(`${this.apiUrl}/${id}`);
  }

  save(district: DistrictRequestModel): Observable<DistrictResponseModel> {
    return this.http.post<DistrictResponseModel>(this.apiUrl, district);
  }

  update(id: number, district: DistrictRequestModel): Observable<DistrictResponseModel> {
    return this.http.put<DistrictResponseModel>(`${this.apiUrl}/${id}`, district);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
  
    getByDivisionId(id: number): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}${id}`);
}

}