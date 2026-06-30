import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { StorageService } from '../../../services/storage.service';

@Component({
  selector: 'app-role-redirect',
  standalone: true,
  templateUrl: './role-redirect.component.html',
  styles: []
})
export class RoleRedirectComponent implements OnInit {
  constructor(private storageService: StorageService, private router: Router) {}

  ngOnInit(): void {
    const role = this.storageService.getRole();
    if (!role) {
      this.router.navigate(['/login']);
      return;
    }

    // Map each role to their respective feature page or shared main dashboard
    switch (role.toUpperCase()) {
      case 'ADMIN':
      case 'MANAGER':
        this.router.navigate(['/feature/agents']);
        break;
      case 'CUSTOMER':
        this.router.navigate(['/feature/customer']);
        break;
      case 'DRIVER':
        this.router.navigate(['/feature/riders']);
        break;
      default:
        this.router.navigate(['/feature/customer']);
        break;
    }
  }
}
