import { Component, OnInit } from '@angular/core';
import { AdminService, AdminUser } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-users',
  templateUrl: './admin-users.component.html'
})
export class AdminUsersComponent implements OnInit {
  users: AdminUser[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  searchTerm = '';
  selectedRole = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
  }

  loadUsers(page = 0): void {
    this.loading = true;
    this.adminService.getUsers(
      this.selectedRole || undefined,
      this.searchTerm || undefined,
      page
    ).subscribe({
      next: (data) => {
        this.users = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load users.';
        this.loading = false;
      }
    });
  }

  onSearch(): void {
    this.loadUsers(0);
  }

  updateStatus(user: AdminUser, status: string): void {
    this.adminService.updateUserStatus(user.id, status).subscribe({
      next: (updated) => {
        user.status = updated.status;
        this.successMessage = `User ${user.name} status updated to ${status}.`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to update user status.';
      }
    });
  }

  resetPassword(user: AdminUser): void {
    if (!confirm(`Reset password for ${user.name}?`)) return;
    this.adminService.resetUserPassword(user.id).subscribe({
      next: () => {
        this.successMessage = `Password reset for ${user.name}. User notified.`;
        setTimeout(() => this.successMessage = '', 4000);
      },
      error: () => {
        this.errorMessage = 'Failed to reset password.';
      }
    });
  }

  deactivate(user: AdminUser): void {
    if (!confirm(`Deactivate account for ${user.name}?`)) return;
    this.adminService.deactivateUser(user.id).subscribe({
      next: () => {
        user.status = 'DEACTIVATED';
        this.successMessage = `User ${user.name} deactivated.`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => {
        this.errorMessage = 'Failed to deactivate user.';
      }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadUsers(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadUsers(this.currentPage + 1); }
}
