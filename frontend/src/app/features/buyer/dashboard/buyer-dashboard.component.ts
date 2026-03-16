import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { RfqService } from '../../../core/services/rfq.service';
import { RFQ } from '../../../core/models/rfq.model';

@Component({
  standalone: false,
  selector: 'app-buyer-dashboard',
  templateUrl: './buyer-dashboard.component.html'
})
export class BuyerDashboardComponent implements OnInit {
  rfqs: RFQ[] = [];
  loading = true;
  errorMessage = '';

  constructor(private rfqService: RfqService, private router: Router) {}

  ngOnInit(): void {
    this.loadRFQs();
  }

  loadRFQs(): void {
    this.rfqService.getMyRFQs().subscribe({
      next: (rfqs) => {
        this.rfqs = rfqs;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load your RFQs.';
        this.loading = false;
      }
    });
  }

  viewQuotes(rfqId: number): void {
    this.router.navigate(['/buyer/rfq', rfqId, 'quotes']);
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      OPEN: 'success',
      CLOSED: 'secondary',
      CANCELLED: 'danger',
      EXPIRED: 'warning'
    };
    return map[status] || 'secondary';
  }
}
