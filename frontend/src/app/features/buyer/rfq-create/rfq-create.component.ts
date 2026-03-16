import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { RfqService } from '../../../core/services/rfq.service';

@Component({
  standalone: false,
  selector: 'app-rfq-create',
  templateUrl: './rfq-create.component.html'
})
export class RfqCreateComponent {
  rfqForm: FormGroup;
  loading = false;
  errorMessage = '';

  partCategories = ['Engine', 'Transmission', 'Body', 'Electrical', 'Interior', 'Suspension', 'Brakes', 'Other'];
  conditions = ['NEW', 'USED', 'REFURBISHED'];

  constructor(private fb: FormBuilder, private rfqService: RfqService, private router: Router) {
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

  onSubmit(): void {
    if (this.rfqForm.invalid) {
      this.rfqForm.markAllAsTouched();
      return;
    }
    this.loading = true;
    this.errorMessage = '';
    this.rfqService.createRFQ(this.rfqForm.value).subscribe({
      next: () => this.router.navigate(['/buyer/dashboard']),
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to create RFQ. Please try again.';
        this.loading = false;
      }
    });
  }
}
