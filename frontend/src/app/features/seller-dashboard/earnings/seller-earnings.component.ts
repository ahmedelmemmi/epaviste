import { Component, OnInit } from '@angular/core';
import { SellerService } from '../../../core/services/seller.service';
import { SellerEarnings } from '../../../core/models/seller.model';

@Component({
  standalone: false,
  selector: 'app-seller-earnings',
  templateUrl: './seller-earnings.component.html'
})
export class SellerEarningsComponent implements OnInit {
  earnings: SellerEarnings | null = null;
  loading = true;
  errorMessage = '';

  constructor(private sellerService: SellerService) {}

  ngOnInit(): void {
    this.sellerService.getEarnings().subscribe({
      next: (data) => {
        this.earnings = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load earnings data.';
        this.loading = false;
      }
    });
  }

  getPaymentStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'warning',
      COMPLETED: 'success',
      FAILED: 'danger',
      REFUNDED: 'secondary'
    };
    return map[status] || 'secondary';
  }
}
