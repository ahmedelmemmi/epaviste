import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { Order } from '../../../core/models/order.model';

@Component({
  standalone: false,
  selector: 'app-buyer-orders',
  templateUrl: './buyer-orders.component.html'
})
export class BuyerOrdersComponent implements OnInit {
  orders: Order[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  currentUserId: number | null = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const user = this.authService.currentUser;
    this.currentUserId = user ? user.userId : null;
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        this.orders = data.filter(o => o.buyerId === this.currentUserId);
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load orders.';
        this.loading = false;
      }
    });
  }

  confirmDelivery(orderId: number): void {
    if (!confirm('Confirm that you have received this order?')) return;
    this.orderService.confirmDelivery(orderId).subscribe({
      next: () => {
        this.successMessage = 'Delivery confirmed! You can now leave a review.';
        this.loadOrders();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to confirm delivery.';
      }
    });
  }

  leaveReview(orderId: number): void {
    this.router.navigate(['/buyer-dashboard/reviews'], { queryParams: { orderId } });
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'warning',
      CONFIRMED: 'info',
      SHIPPED: 'primary',
      DELIVERED: 'success',
      CANCELLED: 'secondary',
      DISPUTED: 'danger'
    };
    return map[status] || 'secondary';
  }

  get activeOrders(): Order[] {
    return this.orders.filter(o => ['PENDING', 'CONFIRMED', 'SHIPPED'].includes(o.orderStatus));
  }

  get orderHistory(): Order[] {
    return this.orders.filter(o => ['DELIVERED', 'CANCELLED', 'DISPUTED'].includes(o.orderStatus));
  }
}
