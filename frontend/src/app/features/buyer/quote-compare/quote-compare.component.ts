import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RfqService } from '../../../core/services/rfq.service';
import { QuoteService } from '../../../core/services/quote.service';
import { RFQ } from '../../../core/models/rfq.model';
import { Quote } from '../../../core/models/quote.model';

@Component({
  standalone: false,
  selector: 'app-quote-compare',
  templateUrl: './quote-compare.component.html'
})
export class QuoteCompareComponent implements OnInit {
  rfq: RFQ | null = null;
  quotes: Quote[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  rfqId!: number;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private rfqService: RfqService,
    private quoteService: QuoteService
  ) {}

  ngOnInit(): void {
    this.rfqId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadData();
  }

  loadData(): void {
    this.rfqService.getRFQ(this.rfqId).subscribe({
      next: (rfq) => {
        this.rfq = rfq;
        this.quoteService.getQuotesForRFQ(this.rfqId).subscribe({
          next: (quotes) => {
            this.quotes = quotes;
            this.loading = false;
          },
          error: () => {
            this.errorMessage = 'Failed to load quotes.';
            this.loading = false;
          }
        });
      },
      error: () => {
        this.errorMessage = 'Failed to load RFQ details.';
        this.loading = false;
      }
    });
  }

  acceptQuote(quoteId: number): void {
    this.quoteService.acceptQuote(quoteId).subscribe({
      next: () => {
        this.successMessage = 'Quote accepted! An order has been created.';
        this.loadData();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to accept quote.';
      }
    });
  }

  rejectQuote(quoteId: number): void {
    this.quoteService.rejectQuote(quoteId).subscribe({
      next: () => {
        this.loadData();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to reject quote.';
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
