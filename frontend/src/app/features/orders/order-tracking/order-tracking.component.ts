import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { AuthService } from '../../../core/services/auth.service';
import { Order } from '../../../core/models/order.model';

@Component({
  selector: 'app-order-tracking',
  templateUrl: './order-tracking.component.html'
})
export class OrderTrackingComponent implements OnInit {
  orders: Order[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';

  constructor(private orderService: OrderService, public authService: AuthService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(): void {
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.orders = orders;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load orders.';
        this.loading = false;
      }
    });
  }

  confirmDelivery(orderId: number): void {
    this.orderService.confirmDelivery(orderId).subscribe({
      next: () => {
        this.successMessage = 'Delivery confirmed!';
        this.loadOrders();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to confirm delivery.';
      }
    });
  }

  markShipped(orderId: number): void {
    this.orderService.markShipped(orderId).subscribe({
      next: () => {
        this.successMessage = 'Order marked as shipped!';
        this.loadOrders();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to mark as shipped.';
      }
    });
  }

  getStatusClass(status: string): string {
    const map: Record<string, string> = {
      PENDING: 'secondary',
      CONFIRMED: 'info',
      SHIPPED: 'primary',
      DELIVERED: 'success',
      CANCELLED: 'danger'
    };
    return map[status] || 'secondary';
  }

  getStatusIcon(status: string): string {
    const map: Record<string, string> = {
      PENDING: '🕐',
      CONFIRMED: '✅',
      SHIPPED: '🚚',
      DELIVERED: '📦',
      CANCELLED: '❌'
    };
    return map[status] || '❓';
  }
}
