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
  imports: [CommonModule, RouterModule,FormsModule],
  templateUrl: './header.html',
  styleUrls: ['./header.css'],
})
export class Header implements OnInit, OnDestroy {
  unreadNotificationCount = 0;
  user: any = null;
  showDropdown = false;
  showLogoutModal = false;
  onlineStatus = true; // green active status
  currentDateTime = '';
  private clockInterval: any;

  // Mock profile details
  employeeId = 'EMP-90881';
  departmentName = 'SCM Corporate Node';
  lastLoginTime = 'Today, 08:34 AM';

  constructor(
    private notificationService: NotificationService,
    private storage: StorageService,
    private authService: AuthService,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    this.user = this.storage.getUser();
    this.loadUnreadCount();
    this.updateTime();
    this.clockInterval = setInterval(() => {
      this.updateTime();
    }, 1000);
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
      error: (err) => console.error("Header Sync Interrupted:", err)
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
    } else {
      alert(`Simulation Triggered: ${action}`);
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
    alert('You have been logged out successfully.');
  }
}
