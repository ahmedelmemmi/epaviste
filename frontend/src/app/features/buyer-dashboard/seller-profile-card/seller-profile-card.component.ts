import { Component, Input, OnInit } from '@angular/core';
import { SellerPublicProfile } from '../../../core/models/seller.model';
import { SellerService } from '../../../core/services/seller.service';

@Component({
  standalone: false,
  selector: 'app-seller-profile-card',
  templateUrl: './seller-profile-card.component.html'
})
export class SellerProfileCardComponent implements OnInit {
  @Input() sellerId!: number;

  profile: SellerPublicProfile | null = null;
  loading = true;
  error = false;

  constructor(private sellerService: SellerService) {}

  ngOnInit(): void {
    if (this.sellerId) {
      this.sellerService.getSellerPublicProfile(this.sellerId).subscribe({
        next: (data) => {
          this.profile = data;
          this.loading = false;
        },
        error: () => {
          this.error = true;
          this.loading = false;
        }
      });
    }
  }

  getStars(): number[] {
    return Array(5).fill(0).map((_, i) => i + 1);
  }

  isFullStar(index: number, rating: number): boolean {
    return index <= Math.floor(rating);
  }

  isHalfStar(index: number, rating: number): boolean {
    return index === Math.ceil(rating) && rating % 1 >= 0.5;
  }
}
