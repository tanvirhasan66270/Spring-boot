import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-public-services',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './public-services.component.html',
  styleUrls: ['./public-services.component.css']
})
export class PublicServicesComponent {
  services = [
    { title: 'Global Product Sourcing', desc: 'Secure suppliers worldwide with bidding matrices.' },
    { title: 'Air & Ocean Freight', desc: 'Multimodal container routes carrying priority consignments.' },
    { title: 'Inventory Buffering', desc: 'Automated warehouse status audits and dispatch queues.' },
    { title: 'Customs & HS Code Verification', desc: 'Clearance logs aligning proforma billing and bank matching.' }
  ];
}
