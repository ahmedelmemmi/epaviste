import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { ReviewService } from '../../../core/services/review.service';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { Order } from '../../../core/models/order.model';

@Component({
  standalone: false,
  selector: 'app-buyer-reviews',
  templateUrl: './buyer-reviews.component.html'
})
export class BuyerReviewsComponent implements OnInit {
  deliveredOrders: Order[] = [];
  loading = true;
  submitting = false;
  errorMessage = '';
  successMessage = '';
  reviewForm!: FormGroup;
  selectedOrder: Order | null = null;
  currentUserId: number | null = null;
  ratings = [1, 2, 3, 4, 5];

  constructor(
    private reviewService: ReviewService,
    private orderService: OrderService,
    private authService: AuthService,
    private route: ActivatedRoute,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser;
    this.currentUserId = user ? user.userId : null;
    this.reviewForm = this.fb.group({
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      comment: ['', Validators.required]
    });
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        this.deliveredOrders = data.filter(
          o => o.buyerId === this.currentUserId && o.orderStatus === 'DELIVERED'
        );
        this.loading = false;
        const orderId = this.route.snapshot.queryParamMap.get('orderId');
        if (orderId) {
          const found = this.deliveredOrders.find(o => o.id === +orderId);
          if (found) this.selectOrder(found);
        }
      },
      error: () => {
        this.errorMessage = 'Failed to load orders.';
        this.loading = false;
      }
    });
  }

  selectOrder(order: Order): void {
    this.selectedOrder = order;
    this.reviewForm.reset({ rating: 5, comment: '' });
    this.successMessage = '';
    this.errorMessage = '';
  }

  submitReview(): void {
    if (this.reviewForm.invalid || !this.selectedOrder) return;
    this.submitting = true;
    this.errorMessage = '';
    this.reviewService.submitReview({
      orderId: this.selectedOrder.id,
      rating: this.reviewForm.value.rating,
      comment: this.reviewForm.value.comment
    }).subscribe({
      next: () => {
        this.successMessage = 'Review submitted successfully!';
        this.submitting = false;
        this.selectedOrder = null;
        this.reviewForm.reset({ rating: 5, comment: '' });
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to submit review.';
        this.submitting = false;
      }
    });
  }
}
