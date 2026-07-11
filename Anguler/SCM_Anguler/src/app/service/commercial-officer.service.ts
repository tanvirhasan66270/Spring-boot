import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environment/environment';
import { CommercialOfficerRequestDTO, CommercialOfficerResponseDTO } from '../component/shared/model/customerModel';
@Injectable({
  providedIn: 'root',
})
export class CommercialOfficerService {
  private apiUrl = environment.apiUrl + "commercial-officer";

  constructor(private http: HttpClient) { }

  findAll(): Observable<CommercialOfficerResponseDTO[]> {
    return this.http.get<CommercialOfficerResponseDTO[]>(this.apiUrl);
  }

  getById(id: number): Observable<CommercialOfficerResponseDTO> {
    return this.http.get<CommercialOfficerResponseDTO>(`${this.apiUrl}/${id}`);
  }

  save(officer: CommercialOfficerRequestDTO, file: File | null): Observable<CommercialOfficerResponseDTO> {
    const formData = new FormData();
    
    formData.append(
      "commercialOfficer",
      new Blob([JSON.stringify(officer)], { type: "application/json" })
    );

    if (file) {
      formData.append("file", file);
    }

    return this.http.post<CommercialOfficerResponseDTO>(this.apiUrl, formData);
  }

  update(id: number, officer: CommercialOfficerRequestDTO, file: File | null): Observable<CommercialOfficerResponseDTO> {
    const formData = new FormData();
    
    formData.append(
      "officer",
      new Blob([JSON.stringify(officer)], { type: "application/json" })
    );

    if (file) {
      formData.append("file", file);
    }

    return this.http.put<CommercialOfficerResponseDTO>(`${this.apiUrl}/${id}`, formData);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
