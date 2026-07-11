import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-public-about',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './public-about.component.html',
  styleUrls: ['./public-about.component.css']
})
export class PublicAboutComponent {
  coreValues = [
    { title: 'Reliability', desc: 'Ensuring on-time deliveries with strict compliance.' },
    { title: 'Global Network', desc: 'Over 150+ ports and supply hubs integrated globally.' },
    { title: 'Green Logistics', desc: 'Committed to decreasing carbon footprints via green transport.' }
  ];
}
