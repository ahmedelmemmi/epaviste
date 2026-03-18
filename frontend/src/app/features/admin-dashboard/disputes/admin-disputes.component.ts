import { Component, OnInit } from '@angular/core';
import { AdminService, AdminDispute } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-disputes',
  templateUrl: './admin-disputes.component.html'
})
export class AdminDisputesComponent implements OnInit {
  disputes: AdminDispute[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  resolutionText = '';
  selectedDisputeId: number | null = null;
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadDisputes();
  }

  loadDisputes(page = 0): void {
    this.loading = true;
    this.adminService.getDisputes(page).subscribe({
      next: (data) => {
        this.disputes = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load disputes.';
        this.loading = false;
      }
    });
  }

  openResolveModal(id: number): void {
    this.selectedDisputeId = id;
    this.resolutionText = '';
  }

  submitResolution(): void {
    if (!this.selectedDisputeId || !this.resolutionText.trim()) return;
    this.adminService.resolveDispute(this.selectedDisputeId, this.resolutionText).subscribe({
      next: () => {
        this.successMessage = `Dispute #${this.selectedDisputeId} resolved.`;
        this.selectedDisputeId = null;
        this.loadDisputes(this.currentPage);
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to resolve dispute.'; }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadDisputes(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadDisputes(this.currentPage + 1); }
}
