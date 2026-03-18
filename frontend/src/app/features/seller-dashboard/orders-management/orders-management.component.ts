import { Component, OnInit } from '@angular/core';
import { OrderService } from '../../../core/services/order.service';
import { Order } from '../../../core/models/order.model';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  standalone: false,
  selector: 'app-orders-management',
  templateUrl: './orders-management.component.html'
})
export class OrdersManagementComponent implements OnInit {
  orders: Order[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  currentUserId: number | null = null;

  constructor(private orderService: OrderService, private authService: AuthService) {}

  ngOnInit(): void {
    const user = this.authService.currentUser;
    this.currentUserId = user ? user.userId : null;
    this.loadOrders();
  }

  loadOrders(): void {
    this.loading = true;
    this.orderService.getMyOrders().subscribe({
      next: (data) => {
        this.orders = data.filter(o => o.sellerId === this.currentUserId);
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load orders.';
        this.loading = false;
      }
    });
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

  markShipped(orderId: number): void {
    this.orderService.markShipped(orderId).subscribe({
      next: () => {
        this.successMessage = 'Order marked as shipped!';
        this.loadOrders();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update order status.';
      }
    });
  }

  updateStatus(orderId: number, status: string): void {
    this.orderService.updateOrderStatus(orderId, status).subscribe({
      next: () => {
        this.successMessage = `Order status updated to ${status}.`;
        this.loadOrders();
      },
      error: (err) => {
        this.errorMessage = err.error?.message || 'Failed to update order status.';
      }
    });
  }
}
