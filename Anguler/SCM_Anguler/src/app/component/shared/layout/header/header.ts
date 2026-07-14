import { Component, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { NotificationService } from '../../../../system/service/notification.service';
import { StorageService } from '../../../../auth/auth_service/storage.service';
import { AuthService } from '../../../../auth/auth_service/auth-service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
})
export class Header implements OnInit, OnDestroy {
  unreadNotificationCount = 0;
  user: any = null;
  showDropdown = false;
  showLogoutModal = false;
  onlineStatus = true;
  currentDateTime = '';
  private clockInterval: any;

  employeeId = '';
  departmentName = 'SCM Corporate Node';

  constructor(
    private notificationService: NotificationService,
    private storage: StorageService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit() {
    this.user = this.storage.getUser();
    if (this.user) {
      this.employeeId = `EMP-${this.user.userId}`;
      this.departmentName = this.getDepartmentName(this.user.role);
    }
    this.loadUnreadCount();
    this.updateTime();
    this.clockInterval = setInterval(() => {
      this.updateTime();
    }, 1000);
  }

  private getDepartmentName(role: string): string {
    const departments: Record<string, string> = {
      ADMIN: 'SCM Administration',
      MANAGER: 'SCM Management',
      PROCUREMENT: 'Procurement Division',
      QC_INSPECTOR: 'Quality Control',
      LOGISTICS_OFFICER: 'Logistics & Fleet',
      COMMERCIAL_OFFICER: 'Commercial Imports',
      SALES_OFFICER: 'Sales Division',
      DRIVER: 'Delivery Operations',
      SUPPLIER: 'Supplier Portal',
      CUSTOMER: 'Customer Portal',
    };
    return departments[role] || 'SCM Corporate Node';
  }

  ngOnDestroy() {
    if (this.clockInterval) {
      clearInterval(this.clockInterval);
    }
  }

  loadUnreadCount() {
    this.notificationService.getUnreadCount().subscribe({
      next: (count) => {
        this.unreadNotificationCount = count;
        this.cdr.markForCheck();
      },
      error: () => {},
    });
  }

  updateTime() {
    const now = new Date();
    this.currentDateTime = now.toLocaleString();
    this.cdr.markForCheck();
  }

  toggleDropdown() {
    this.showDropdown = !this.showDropdown;
    this.cdr.markForCheck();
  }

  toggleOnlineStatus() {
    this.onlineStatus = !this.onlineStatus;
    this.cdr.markForCheck();
  }

  getInitials(): string {
    if (!this.user?.name) return 'U';
    const parts = this.user.name.split(' ');
    if (parts.length > 1) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return parts[0][0].toUpperCase();
  }

  handleDropdownAction(action: string) {
    this.showDropdown = false;
    if (action === 'logout') {
      this.showLogoutModal = true;
    } else if (action === 'theme') {
      this.toggleTheme();
    }
    this.cdr.markForCheck();
  }

  toggleTheme() {
    const isDark = document.body.classList.toggle('dark-theme');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
  }

  closeLogoutModal() {
    this.showLogoutModal = false;
    this.cdr.markForCheck();
  }

  confirmLogout() {
    this.showLogoutModal = false;
    this.authService.logout();
  }

  closeDropdown(event: MouseEvent) {
    const target = event.target as HTMLElement;
    if (!target.closest('.profile-container')) {
      this.showDropdown = false;
      this.cdr.markForCheck();
    }
  }
}
