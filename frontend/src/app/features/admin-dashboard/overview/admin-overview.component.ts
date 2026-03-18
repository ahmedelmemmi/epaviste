import { Component, OnInit } from '@angular/core';
import { AdminService, AdminStats } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-overview',
  templateUrl: './admin-overview.component.html'
})
export class AdminOverviewComponent implements OnInit {
  stats: AdminStats | null = null;
  loading = true;
  errorMessage = '';

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load platform statistics.';
        this.loading = false;
      }
    });
  }
}
