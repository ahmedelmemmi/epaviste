import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RfqService } from '../../../core/services/rfq.service';
import { QuoteService } from '../../../core/services/quote.service';
import { RFQ } from '../../../core/models/rfq.model';
import { Page } from '../../../core/models/page.model';

@Component({
  selector: 'app-rfq-list',
  templateUrl: './rfq-list.component.html'
})
export class RfqListComponent implements OnInit {
  rfqs: RFQ[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  currentPage = 0;
  totalPages = 1;
  pageSize = 10;

  activeQuoteFormRfqId: number | null = null;
  quoteForm!: FormGroup;
  quoteLoading = false;
  quoteError = '';

  conditions = ['NEW', 'USED', 'REFURBISHED'];
  shippingMethods = ['Standard', 'Express', 'Pickup'];

  constructor(private rfqService: RfqService, private quoteService: QuoteService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadRFQs();
  }

  loadRFQs(): void {
    this.loading = true;
    this.rfqService.getOpenRFQs(this.currentPage, this.pageSize).subscribe({
      next: (data: Page<RFQ>) => {
        this.rfqs = data.content;
        this.totalPages = data.totalPages || 1;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load RFQs.';
        this.loading = false;
      }
    });
  }

  openQuoteForm(rfqId: number): void {
    this.activeQuoteFormRfqId = rfqId;
    this.quoteError = '';
    this.quoteForm = this.fb.group({
      rfqId: [rfqId, Validators.required],
      price: ['', [Validators.required, Validators.min(0.01)]],
      condition: ['NEW', Validators.required],
      deliveryTime: ['', [Validators.required, Validators.min(1)]],
      shippingMethod: ['Standard', Validators.required],
      message: ['', Validators.required]
    });
  }

  closeQuoteForm(): void {
    this.activeQuoteFormRfqId = null;
    this.quoteError = '';
  }

  submitQuote(): void {
    if (this.quoteForm.invalid) {
      this.quoteForm.markAllAsTouched();
      return;
    }
    this.quoteLoading = true;
    this.quoteError = '';
    this.quoteService.submitQuote(this.quoteForm.value).subscribe({
      next: () => {
        this.successMessage = 'Quote submitted successfully!';
        this.activeQuoteFormRfqId = null;
        this.quoteLoading = false;
        this.loadRFQs();
      },
      error: (err) => {
        this.quoteError = err.error?.message || 'Failed to submit quote.';
        this.quoteLoading = false;
      }
    });
  }

  prevPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.loadRFQs();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.loadRFQs();
    }
  }
}
