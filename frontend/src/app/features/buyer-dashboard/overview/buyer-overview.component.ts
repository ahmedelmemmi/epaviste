import { Component, OnInit } from '@angular/core';
import { BuyerService } from '../../../core/services/buyer.service';
import { BuyerStats } from '../../../core/models/buyer.model';

@Component({
  standalone: false,
  selector: 'app-buyer-overview',
  templateUrl: './buyer-overview.component.html'
})
export class BuyerOverviewComponent implements OnInit {
  stats: BuyerStats | null = null;
  loading = true;
  errorMessage = '';

  constructor(private buyerService: BuyerService) {}

  ngOnInit(): void {
    this.buyerService.getDashboardStats().subscribe({
      next: (data) => {
        this.stats = data;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load dashboard statistics.';
        this.loading = false;
      }
    });
  }
}
