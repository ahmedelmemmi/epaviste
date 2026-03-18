import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RfqService } from '../../../core/services/rfq.service';
import { QuoteService } from '../../../core/services/quote.service';
import { RFQ } from '../../../core/models/rfq.model';
import { Quote } from '../../../core/models/quote.model';

@Component({
  standalone: false,
  selector: 'app-buyer-quote-comparison',
  templateUrl: './quote-comparison.component.html'
})
export class BuyerQuoteComparisonComponent implements OnInit {
  rfqs: RFQ[] = [];
  selectedRFQ: RFQ | null = null;
  quotes: Quote[] = [];
  loadingRFQs = true;
  loadingQuotes = false;
  errorMessage = '';
  successMessage = '';
  acceptingId: number | null = null;

  constructor(
    private rfqService: RfqService,
    private quoteService: QuoteService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.rfqService.getMyRFQs().subscribe({
      next: (data) => {
        this.rfqs = data.filter(r => r.quoteCount > 0);
        this.loadingRFQs = false;
        const rfqId = this.route.snapshot.queryParamMap.get('rfqId');
        if (rfqId) {
          const found = this.rfqs.find(r => r.id === +rfqId);
          if (found) this.selectRFQ(found);
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load RFQs.';
        this.loadingRFQs = false;
      }
    });
  }

  selectRFQ(rfq: RFQ): void {
    this.selectedRFQ = rfq;
    this.quotes = [];
    this.loadingQuotes = true;
    this.errorMessage = '';
    this.quoteService.getQuotesForRFQ(rfq.id).subscribe({
      next: (data) => {
        this.quotes = data;
        this.loadingQuotes = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load quotes.';
        this.loadingQuotes = false;
      }
    });
  }

  acceptQuote(quoteId: number): void {
    if (!confirm('Accept this quote and create an order?')) return;
    this.acceptingId = quoteId;
    this.errorMessage = '';
    this.quoteService.acceptQuote(quoteId).subscribe({
      next: () => {
        this.successMessage = 'Quote accepted! An order has been created.';
        this.acceptingId = null;
        if (this.selectedRFQ) this.selectRFQ(this.selectedRFQ);
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to accept quote.';
        this.acceptingId = null;
      }
    });
  }

  rejectQuote(quoteId: number): void {
    this.quoteService.rejectQuote(quoteId).subscribe({
      next: () => {
        this.successMessage = 'Quote rejected.';
        if (this.selectedRFQ) this.selectRFQ(this.selectedRFQ);
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
      REJECTED: 'secondary'
    };
    return map[status] || 'secondary';
  }
}
