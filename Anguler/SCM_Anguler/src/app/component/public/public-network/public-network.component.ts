import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-public-network',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './public-network.component.html',
  styleUrls: ['./public-network.component.css']
})
export class PublicNetworkComponent {
  hubs = [
    { continent: 'Asia', ports: 'Port of Shanghai, Port of Chittagong, Singapore Hub' },
    { continent: 'Europe', ports: 'Rotterdam Hub, Port of Hamburg, London Gateway' },
    { continent: 'Americas', ports: 'Port of Long Beach, New York Terminal, Houston Hub' }
  ];
}
