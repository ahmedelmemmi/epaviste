import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RfqService } from '../../../core/services/rfq.service';
import { RFQ } from '../../../core/models/rfq.model';

@Component({
  standalone: false,
  selector: 'app-buyer-rfq-management',
  templateUrl: './rfq-management.component.html'
})
export class BuyerRfqManagementComponent implements OnInit {
  rfqs: RFQ[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  showCreateForm = false;
  submitting = false;
  rfqForm!: FormGroup;

  conditions = ['NEW', 'USED', 'REFURBISHED'];

  constructor(
    private rfqService: RfqService,
    private fb: FormBuilder,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadRFQs();
  }

  initForm(): void {
    this.rfqForm = this.fb.group({
      carBrand: ['', Validators.required],
      carModel: ['', Validators.required],
      carYear: ['', [Validators.required, Validators.min(1900), Validators.max(new Date().getFullYear() + 1)]],
      vin: [''],
      partName: ['', Validators.required],
      partCategory: ['', Validators.required],
      preferredCondition: ['NEW', Validators.required],
      description: ['', Validators.required],
      location: ['', Validators.required]
    });
  }

  loadRFQs(): void {
    this.loading = true;
    this.rfqService.getMyRFQs().subscribe({
      next: (data) => {
        this.rfqs = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load RFQs.';
        this.loading = false;
      }
    });
  }

  submitRFQ(): void {
    if (this.rfqForm.invalid) return;
    this.submitting = true;
    this.errorMessage = '';
    this.rfqService.createRFQ(this.rfqForm.value).subscribe({
      next: () => {
        this.successMessage = 'RFQ submitted successfully!';
        this.showCreateForm = false;
        this.rfqForm.reset({ preferredCondition: 'NEW' });
        this.submitting = false;
        this.loadRFQs();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to submit RFQ.';
        this.submitting = false;
      }
    });
  }

  cancelRFQ(id: number): void {
    if (!confirm('Are you sure you want to cancel this RFQ?')) return;
    this.rfqService.cancelRFQ(id).subscribe({
      next: () => {
        this.successMessage = 'RFQ cancelled successfully.';
        this.loadRFQs();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to cancel RFQ.';
      }
    });
  }

  viewQuotes(rfqId: number): void {
    this.router.navigate(['/buyer-dashboard/quotes'], { queryParams: { rfqId } });
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      OPEN: 'success',
      CLOSED: 'secondary',
      CANCELLED: 'danger'
    };
    return map[status] || 'secondary';
  }
}
