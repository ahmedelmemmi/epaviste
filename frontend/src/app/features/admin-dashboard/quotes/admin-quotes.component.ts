import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';
import { Quote } from '../../../core/models/quote.model';

@Component({
  standalone: false,
  selector: 'app-admin-quotes',
  templateUrl: './admin-quotes.component.html'
})
export class AdminQuotesComponent implements OnInit {
  quotes: Quote[] = [];
  loading = true;
  errorMessage = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadQuotes();
  }

  loadQuotes(page = 0): void {
    this.loading = true;
    this.adminService.getAllQuotes(page).subscribe({
      next: (data) => {
        this.quotes = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load quotes.';
        this.loading = false;
      }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadQuotes(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadQuotes(this.currentPage + 1); }
}
