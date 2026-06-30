import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class LocationService {
  constructor(private http: HttpClient) {}

  getCountries(): Observable<any[]> {
    return this.http.get<any[]>(environment.apiUrl + 'countries');
  }

  getDivisions(countryId: number): Observable<any[]> {
    return this.http.get<any[]>(environment.apiUrl + `divisions/country/${countryId}`);
  }

  getDistricts(divisionId: number): Observable<any[]> {
    return this.http.get<any[]>(environment.apiUrl + `districts/division/${divisionId}`);
  }

  getPoliceStations(districtId: number): Observable<any[]> {
    return this.http.get<any[]>(environment.apiUrl + `police-stations/district/${districtId}`);
  }
}
