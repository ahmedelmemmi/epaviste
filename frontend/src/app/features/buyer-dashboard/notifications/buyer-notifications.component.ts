import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../../../core/services/notification.service';
import { Notification } from '../../../core/models/notification.model';

@Component({
  standalone: false,
  selector: 'app-buyer-notifications',
  templateUrl: './buyer-notifications.component.html'
})
export class BuyerNotificationsComponent implements OnInit {
  notifications: Notification[] = [];
  loading = true;
  errorMessage = '';

  constructor(private notificationService: NotificationService) {}

  ngOnInit(): void {
    this.loadNotifications();
  }

  loadNotifications(): void {
    this.loading = true;
    this.notificationService.getNotifications().subscribe({
      next: (data) => {
        this.notifications = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load notifications.';
        this.loading = false;
      }
    });
  }

  markAsRead(id: number): void {
    this.notificationService.markAsRead(id).subscribe({
      next: () => this.loadNotifications()
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => this.loadNotifications()
    });
  }

  get unreadCount(): number {
    return this.notifications.filter(n => !n.read).length;
  }
}
