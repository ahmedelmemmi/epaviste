import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';
import { SellerProfile } from '../../../core/models/seller.model';

@Component({
  standalone: false,
  selector: 'app-admin-sellers',
  templateUrl: './admin-sellers.component.html'
})
export class AdminSellersComponent implements OnInit {
  sellers: SellerProfile[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadSellers();
  }

  loadSellers(page = 0): void {
    this.loading = true;
    this.adminService.getPendingSellerVerifications(page).subscribe({
      next: (data) => {
        this.sellers = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load seller verification requests.';
        this.loading = false;
      }
    });
  }

  approve(seller: any): void {
    this.adminService.approveSeller(seller.userId).subscribe({
      next: () => {
        this.successMessage = `${seller.name} approved.`;
        this.loadSellers(this.currentPage);
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to approve seller.'; }
    });
  }

  reject(seller: any): void {
    if (!confirm(`Reject verification for ${seller.name}?`)) return;
    this.adminService.rejectSeller(seller.userId).subscribe({
      next: () => {
        this.successMessage = `${seller.name} rejected.`;
        this.loadSellers(this.currentPage);
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to reject seller.'; }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadSellers(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadSellers(this.currentPage + 1); }
}
