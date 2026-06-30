import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-address',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './address.component.html',
  styles: []
})
export class AddressComponent implements OnInit {
  stations: any[] = [];
  loading = false;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchStations();
  }

  fetchStations(): void {
    this.loading = true;
    this.http.get<any[]>(environment.apiUrl + 'policeStations').subscribe({
      next: (data) => {
        this.stations = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        // Mock list of stations
        this.stations = [
          { id: 1, name: 'Mirpur PS', nameBn: 'মিরপুর মডেল থানা', postalCode: '1216', active: true, districtName: 'Dhaka', divisionName: 'Dhaka' },
          { id: 2, name: 'Uttara PS', nameBn: 'উত্তরা থানা', postalCode: '1230', active: true, districtName: 'Dhaka', divisionName: 'Dhaka' },
          { id: 3, name: 'Kotwali PS', nameBn: 'কোতোয়ালী থানা', postalCode: '4000', active: true, districtName: 'Chittagong', divisionName: 'Chittagong' }
        ];
      }
    });
  }
}
