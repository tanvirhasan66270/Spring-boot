import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { NotificationModel } from '../../NotificationModel';
import { NotificationService } from '../../service/notification.service';


@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './notification.component.html',
  styleUrl: './notification.component.css',
})
export class NotificationComponent implements OnInit {

  notifications: NotificationModel[] = [];
  unreadCount = 0;
  errorMessage: string | null = null;

  constructor(
    private service: NotificationService,
    private cdr: ChangeDetectorRef
  ) { }

  ngOnInit() {
    this.loadNotifications();
  }

  /**
   * ডাটাবেজ থেকে লাইভ নোটিফিকেশন ফিড লোড করা
   */
  loadNotifications() {
    this.errorMessage = null;
    this.service.findAll().subscribe({
      next: (data) => {
        this.notifications = data || [];
        this.updateUnreadCount();
        this.cdr.markForCheck();
      },
      error: (err) => this.handleError(err)
    });
  }

  /**
   * আনরিড ব্যাজ কাউন্টার সিঙ্ক করা
   */
  updateUnreadCount() {
    this.service.getUnreadCount().subscribe({
      next: (count) => {
        this.unreadCount = count;
        this.cdr.markForCheck();
      }
    });
  }

  /**
   * কোনো নোটিফিকেশনে ক্লিক করলে সেটি রিড হিসেবে মার্ক হবে
   */
  toggleRead(notification: NotificationModel) {
    if (notification.isRead || !notification.id) return;

    this.service.markAsRead(notification.id).subscribe({
      next: () => {
        notification.isRead = true;
        this.updateUnreadCount();
        this.cdr.markForCheck();
      },
      error: (err) => this.handleError(err)
    });
  }

  /**
   * সমস্ত নোটিফিকেশন একসাথে রিড মার্ক করা
   */
  clearAllUnread() {
    if (this.unreadCount === 0) return;

    this.service.markAllAsRead().subscribe({
      next: () => {
        this.notifications.forEach(n => n.isRead = true);
        this.unreadCount = 0;
        this.cdr.markForCheck();
        alert("All notifications marked as read.");
      },
      error: (err) => this.handleError(err)
    });
  }

  private handleError(err: any) {
    this.errorMessage = err.error?.message || err.message || "Notification Gateway Connection Timeout.";
    this.cdr.markForCheck();
  }
}
