import { Component, OnInit } from '@angular/core';
import { StorageService } from '../../../../services/storage.service';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styles: []
})
export class SidebarComponent implements OnInit {
  role = '';

  constructor(private storageService: StorageService) {}

  ngOnInit(): void {
    this.role = this.storageService.getRole() || '';
  }
}
