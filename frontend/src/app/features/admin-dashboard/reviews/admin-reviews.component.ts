import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-reviews',
  templateUrl: './admin-reviews.component.html'
})
export class AdminReviewsComponent implements OnInit {
  reviews: any[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadReviews();
  }

  loadReviews(page = 0): void {
    this.loading = true;
    this.adminService.getAllReviews(page).subscribe({
      next: (data) => {
        this.reviews = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load reviews.';
        this.loading = false;
      }
    });
  }

  deleteReview(review: any): void {
    if (!confirm(`Delete review #${review.id}?`)) return;
    this.adminService.deleteReview(review.id).subscribe({
      next: () => {
        this.reviews = this.reviews.filter(r => r.id !== review.id);
        this.successMessage = `Review #${review.id} deleted.`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to delete review.'; }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadReviews(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadReviews(this.currentPage + 1); }
}
