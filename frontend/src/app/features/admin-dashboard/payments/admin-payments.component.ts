import { Component, OnInit } from '@angular/core';
import { AdminService, AdminStats } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-payments',
  templateUrl: './admin-payments.component.html'
})
export class AdminPaymentsComponent implements OnInit {
  revenue: AdminStats | null = null;
  transactions: any[] = [];
  loading = true;
  errorMessage = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.adminService.getPlatformRevenue().subscribe({
      next: (data) => { this.revenue = data; },
      error: () => {}
    });
    this.loadTransactions();
  }

  loadTransactions(page = 0): void {
    this.loading = true;
    this.adminService.getTransactionHistory(page).subscribe({
      next: (data) => {
        this.transactions = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load transactions.';
        this.loading = false;
      }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadTransactions(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadTransactions(this.currentPage + 1); }
}
