import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './customer.component.html',
  styles: []
})
export class CustomerComponent implements OnInit {
  orders: any[] = [];
  loading = false;
  searchOrderNumber = '';
  searchedOrder: any = null;

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchOrders();
  }

  fetchOrders(): void {
    this.loading = true;
    this.http.get<any[]>(environment.apiUrl + 'customerOrders').subscribe({
      next: (data) => {
        this.orders = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        // Mock list of orders for customer
        this.orders = [
          { id: 1, orderNumber: 'ORD-1719799200000', customerName: 'Main Customer', totalAmount: 5200.50, status: 'SHIPPED', serviceType: 'EXPRESS', deliveryAddress: 'Dhaka, Bangladesh', estimatedDelivery: '2026-07-05' },
          { id: 2, orderNumber: 'ORD-1719803200000', customerName: 'Main Customer', totalAmount: 1800.00, status: 'PENDING', serviceType: 'STANDARD', deliveryAddress: 'Chittagong, Bangladesh', estimatedDelivery: '2026-07-10' }
        ];
      }
    });
  }

  trackOrder(): void {
    if (!this.searchOrderNumber) return;

    this.loading = true;
    this.http.get<any>(environment.apiUrl + 'customerOrders/track', {
      params: { orderNumber: this.searchOrderNumber }
    }).subscribe({
      next: (data) => {
        this.searchedOrder = data;
        this.loading = false;
      },
      error: () => {
        this.loading = false;
        // Mock tracking result
        this.searchedOrder = this.orders.find(o => o.orderNumber === this.searchOrderNumber) || {
          orderNumber: this.searchOrderNumber,
          customerName: 'Guest User',
          totalAmount: 3500.00,
          status: 'PENDING',
          serviceType: 'STANDARD',
          deliveryAddress: 'Mirpur, Dhaka',
          estimatedDelivery: '2026-07-08'
        };
      }
    });
  }
}
