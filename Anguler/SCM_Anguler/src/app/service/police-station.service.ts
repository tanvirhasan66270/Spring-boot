import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../encironment/environment';
import { PoliceStationRequestModel, PoliceStationResponseModel } from '../component/shared/model/policeStationModel';

@Injectable({
  providedIn: 'root',
})
export class PoliceStationService {
  private apiUrl = environment.apiUrl + "policestation"; 

  constructor(private http: HttpClient) { }

  getAll(): Observable<PoliceStationResponseModel[]> {
    return this.http.get<PoliceStationResponseModel[]>(this.apiUrl);
  }

  getById(id: number): Observable<PoliceStationResponseModel> {
    return this.http.get<PoliceStationResponseModel>(`${this.apiUrl}/${id}`);
  }

  save(station: PoliceStationRequestModel): Observable<PoliceStationResponseModel> {
    return this.http.post<PoliceStationResponseModel>(this.apiUrl, station);
  }

  update(id: number, station: PoliceStationRequestModel): Observable<PoliceStationResponseModel> {
    return this.http.put<PoliceStationResponseModel>(`${this.apiUrl}/${id}`, station);
  }

  delete(id: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
  }
   getByDistrictId(id: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/district/${id}`);
  }

  search(keyword: string): Observable<any[]> {
  return this.http.get<any[]>(`${this.apiUrl}/search?keyword=${keyword}`);
}
}