import { Component, OnInit } from '@angular/core';
import { QuoteService } from '../../../core/services/quote.service';
import { Quote } from '../../../core/models/quote.model';

@Component({
  selector: 'app-seller-dashboard',
  templateUrl: './seller-dashboard.component.html'
})
export class SellerDashboardComponent implements OnInit {
  quotes: Quote[] = [];
  loading = true;
  errorMessage = '';

  get totalQuotes(): number { return this.quotes.length; }
  get pendingQuotes(): number { return this.quotes.filter(q => q.status === 'PENDING').length; }
  get acceptedQuotes(): number { return this.quotes.filter(q => q.status === 'ACCEPTED').length; }

  constructor(private quoteService: QuoteService) {}

  ngOnInit(): void {
    this.quoteService.getMyQuotes().subscribe({
      next: (quotes) => {
        this.quotes = quotes;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load your quotes.';
        this.loading = false;
      }
    });
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'warning',
      ACCEPTED: 'success',
      REJECTED: 'danger'
    };
    return map[status] || 'secondary';
  }
}
