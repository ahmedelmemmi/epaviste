import { Component, OnInit } from '@angular/core';
import { SellerService } from '../../../core/services/seller.service';
import { SellerStats } from '../../../core/models/seller.model';

@Component({
  standalone: false,
  selector: 'app-seller-overview',
  templateUrl: './seller-overview.component.html'
})
export class SellerOverviewComponent implements OnInit {
  stats: SellerStats | null = null;
  loading = true;
  errorMessage = '';

  constructor(private sellerService: SellerService) {}

  ngOnInit(): void {
    this.sellerService.getDashboardStats().subscribe({
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
