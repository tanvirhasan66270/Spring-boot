import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-public-landing',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './public-landing.component.html',
  styleUrls: ['./public-landing.component.css']
})
export class PublicLandingComponent {
  trackingNumber = '';
  trackingDetails: any = null;
  showDetails = false;

  trackShipment(): void {
    if (!this.trackingNumber.trim()) {
      alert('Please enter a valid tracking reference.');
      return;
    }
    this.trackingDetails = {
      ref: this.trackingNumber.toUpperCase(),
      status: 'DEPARTED TRANSSHIPMENT HUB',
      origin: 'Port of Shanghai (CN)',
      destination: 'Port of Chittagong (BD)',
      vessel: 'MSC Isabelle II',
      eta: 'July 22, 2026',
      progress: 42
    };
    this.showDetails = true;
  }
}
