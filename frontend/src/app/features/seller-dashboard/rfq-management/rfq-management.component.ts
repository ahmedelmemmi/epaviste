import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SellerService } from '../../../core/services/seller.service';
import { QuoteService } from '../../../core/services/quote.service';
import { RfqService } from '../../../core/services/rfq.service';
import { RFQ } from '../../../core/models/rfq.model';
import { Page } from '../../../core/models/page.model';

@Component({
  standalone: false,
  selector: 'app-rfq-management',
  templateUrl: './rfq-management.component.html'
})
export class RfqManagementComponent implements OnInit {
  rfqs: RFQ[] = [];
  selectedRFQ: RFQ | null = null;
  loading = true;
  errorMessage = '';
  successMessage = '';
  currentPage = 0;
  totalPages = 1;
  pageSize = 10;

  filterForm!: FormGroup;
  quoteForm!: FormGroup;
  quoteLoading = false;
  quoteError = '';
  activeQuoteFormRfqId: number | null = null;

  conditions = ['NEW', 'USED', 'REFURBISHED'];
  shippingMethods = ['Standard', 'Express', 'Pickup'];

  constructor(
    private sellerService: SellerService,
    private rfqService: RfqService,
    private quoteService: QuoteService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.filterForm = this.fb.group({
      carBrand: [''],
      carModel: [''],
      partCategory: [''],
      location: ['']
    });
    this.loadRFQs();
  }

  loadRFQs(): void {
    this.loading = true;
    const f = this.filterForm.value;
    this.sellerService.getSellerRFQs(
      f.carBrand || undefined,
      f.carModel || undefined,
      f.partCategory || undefined,
      f.location || undefined,
      this.currentPage,
      this.pageSize
    ).subscribe({
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

  applyFilters(): void {
    this.currentPage = 0;
    this.loadRFQs();
  }

  clearFilters(): void {
    this.filterForm.reset();
    this.currentPage = 0;
    this.loadRFQs();
  }

  viewRFQDetail(rfq: RFQ): void {
    this.selectedRFQ = rfq;
    this.activeQuoteFormRfqId = null;
    this.quoteError = '';
    this.successMessage = '';
  }

  closeDetail(): void {
    this.selectedRFQ = null;
    this.activeQuoteFormRfqId = null;
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
        if (this.selectedRFQ) {
          this.rfqService.getRFQ(this.selectedRFQ.id).subscribe(updated => {
            this.selectedRFQ = updated;
          });
        }
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
