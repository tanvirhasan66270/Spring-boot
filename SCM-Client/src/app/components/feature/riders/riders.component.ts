import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-riders',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './riders.component.html',
  styles: []
})
export class RidersComponent implements OnInit {
  trips: any[] = [];
  loading = false;
  message = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchTrips();
  }

  fetchTrips(): void {
    this.loading = true;
    this.http.get<any[]>(environment.apiUrl + 'deliveryTrips').subscribe({
      next: (data) => {
        this.trips = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        // Mock list of driver trips
        this.trips = [
          { id: 1, sendBy: 'SCM Depot 1', status: 'IN_TRANSIT', vehicleInfo: 'Truck (Metro-T-1244)', destinationInfo: 'Mirpur-10, Dhaka', scheduleInfo: 'Morning shift', tripInfo: 'Delivery to 5 customer points', startedAt: '2026-07-01 08:30:00' },
          { id: 2, sendBy: 'SCM Central Hub', status: 'PENDING', vehicleInfo: 'Van (Metro-M-9912)', destinationInfo: 'Dhanmondi, Dhaka', scheduleInfo: 'Afternoon shift', tripInfo: 'Delivery to 3 customer points', startedAt: null }
        ];
      }
    });
  }

  startTrip(trip: any): void {
    trip.status = 'IN_TRANSIT';
    trip.startedAt = new Date().toLocaleString();
    this.message = `Trip ID-${trip.id} marked as IN TRANSIT.`;
    setTimeout(() => this.message = '', 3000);
  }

  completeTrip(trip: any): void {
    trip.status = 'DELIVERED';
    trip.completedAt = new Date().toLocaleString();
    this.message = `Trip ID-${trip.id} marked as DELIVERED.`;
    setTimeout(() => this.message = '', 3000);
  }
}
