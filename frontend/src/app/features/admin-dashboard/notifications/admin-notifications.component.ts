import { Component } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-notifications',
  templateUrl: './admin-notifications.component.html'
})
export class AdminNotificationsComponent {
  type = '';
  message = '';
  targetRole = '';
  sending = false;
  successMessage = '';
  errorMessage = '';

  constructor(private adminService: AdminService) {}

  send(): void {
    if (!this.type.trim() || !this.message.trim()) return;
    this.sending = true;
    this.adminService.broadcastNotification(
      this.type, this.message, this.targetRole || undefined
    ).subscribe({
      next: () => {
        this.successMessage = 'Notification sent successfully.';
        this.type = '';
        this.message = '';
        this.targetRole = '';
        this.sending = false;
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: () => {
        this.errorMessage = 'Failed to send notification.';
        this.sending = false;
      }
    });
  }
}
