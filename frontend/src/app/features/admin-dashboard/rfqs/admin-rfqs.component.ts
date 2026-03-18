import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-rfqs',
  templateUrl: './admin-rfqs.component.html'
})
export class AdminRfqsComponent implements OnInit {
  rfqs: any[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadRFQs();
  }

  loadRFQs(page = 0): void {
    this.loading = true;
    this.adminService.getAllRFQs(page).subscribe({
      next: (data) => {
        this.rfqs = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load RFQs.';
        this.loading = false;
      }
    });
  }

  close(rfq: any): void {
    this.adminService.closeRFQ(rfq.id).subscribe({
      next: (updated) => {
        rfq.status = updated.status;
        this.successMessage = `RFQ #${rfq.id} closed.`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to close RFQ.'; }
    });
  }

  delete(rfq: any): void {
    if (!confirm(`Delete RFQ #${rfq.id}?`)) return;
    this.adminService.deleteRFQ(rfq.id).subscribe({
      next: () => {
        this.rfqs = this.rfqs.filter(r => r.id !== rfq.id);
        this.successMessage = `RFQ #${rfq.id} deleted.`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to delete RFQ.'; }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadRFQs(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadRFQs(this.currentPage + 1); }
}
