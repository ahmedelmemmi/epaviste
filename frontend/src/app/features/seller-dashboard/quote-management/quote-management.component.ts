import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { QuoteService } from '../../../core/services/quote.service';
import { Quote } from '../../../core/models/quote.model';

@Component({
  standalone: false,
  selector: 'app-quote-management',
  templateUrl: './quote-management.component.html'
})
export class QuoteManagementComponent implements OnInit {
  quotes: Quote[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';

  editingQuoteId: number | null = null;
  editForm!: FormGroup;
  editLoading = false;
  editError = '';

  conditions = ['NEW', 'USED', 'REFURBISHED'];
  shippingMethods = ['Standard', 'Express', 'Pickup'];

  constructor(private quoteService: QuoteService, private fb: FormBuilder) {}

  ngOnInit(): void {
    this.loadQuotes();
  }

  loadQuotes(): void {
    this.loading = true;
    this.quoteService.getMyQuotes().subscribe({
      next: (data) => {
        this.quotes = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load quotes.';
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

  openEditForm(quote: Quote): void {
    this.editingQuoteId = quote.id;
    this.editError = '';
    this.editForm = this.fb.group({
      price: [quote.price, [Validators.required, Validators.min(0.01)]],
      condition: [quote.condition, Validators.required],
      deliveryTime: [quote.deliveryTime, [Validators.required, Validators.min(1)]],
      shippingMethod: [quote.shippingMethod, Validators.required],
      message: [quote.message, Validators.required]
    });
  }

  closeEditForm(): void {
    this.editingQuoteId = null;
    this.editError = '';
  }

  saveEdit(): void {
    if (this.editForm.invalid || !this.editingQuoteId) {
      this.editForm.markAllAsTouched();
      return;
    }
    this.editLoading = true;
    this.editError = '';
    this.quoteService.updateQuote(this.editingQuoteId, this.editForm.value).subscribe({
      next: () => {
        this.successMessage = 'Quote updated successfully!';
        this.editingQuoteId = null;
        this.editLoading = false;
        this.loadQuotes();
      },
      error: (err) => {
        this.editError = err.error?.message || 'Failed to update quote.';
        this.editLoading = false;
      }
    });
  }

  withdrawQuote(id: number): void {
    if (!confirm('Are you sure you want to withdraw this quote? This cannot be undone.')) return;
    this.quoteService.withdrawQuote(id).subscribe({
      next: () => {
        this.successMessage = 'Quote withdrawn successfully.';
        this.loadQuotes();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to withdraw quote.';
      }
    });
  }
}
