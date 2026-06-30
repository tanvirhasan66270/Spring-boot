import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-booking-receipt',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking-receipt.component.html',
  styles: [`
    @media print {
      .no-print { display: none !important; }
      body { background: white; color: black; }
      .card { border: none !important; box-shadow: none !important; }
    }
  `]
})
export class BookingReceiptComponent implements OnInit {
  orderNumber = '';
  receiptData: any = null;

  constructor(private route: ActivatedRoute) {}

  ngOnInit(): void {
    this.orderNumber = this.route.snapshot.queryParamMap.get('orderNumber') || 'ORD-' + Date.now();
    
    // Fetch or mock receipt details
    this.receiptData = {
      orderNumber: this.orderNumber,
      date: new Date().toLocaleDateString(),
      customerName: 'Main SCM Customer',
      customerEmail: 'customer@scm.com',
      address: 'House 44, Road 11, Dhanmondi, Dhaka',
      itemSubtotal: 4800.00,
      deliveryCharge: 120.00,
      codAmount: 0.0,
      totalAmount: 4920.00,
      paidAmount: '4920.00',
      currency: 'BDT',
      status: 'CONFIRMED',
      items: [
        { productName: 'Standard Cargo Box A', quantity: 2, unitPrice: 1500.00, total: 3000.00 },
        { productName: 'Fragile Distribution Wrap B', quantity: 3, unitPrice: 600.00, total: 1800.00 }
      ]
    };
  }

  printReceipt(): void {
    window.print();
  }
}
