import { Component, OnInit } from '@angular/core';
import { AdminService } from '../../../core/services/admin.service';

@Component({
  standalone: false,
  selector: 'app-admin-orders',
  templateUrl: './admin-orders.component.html'
})
export class AdminOrdersComponent implements OnInit {
  orders: any[] = [];
  loading = true;
  errorMessage = '';
  successMessage = '';
  totalPages = 0;
  currentPage = 0;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadOrders();
  }

  loadOrders(page = 0): void {
    this.loading = true;
    this.adminService.getAllOrders(page).subscribe({
      next: (data) => {
        this.orders = data.content;
        this.totalPages = data.totalPages;
        this.currentPage = data.number;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Failed to load orders.';
        this.loading = false;
      }
    });
  }

  cancelOrder(order: any): void {
    if (!confirm(`Cancel order #${order.id}?`)) return;
    this.adminService.cancelOrder(order.id).subscribe({
      next: (updated) => {
        order.orderStatus = updated.orderStatus;
        this.successMessage = `Order #${order.id} cancelled.`;
        setTimeout(() => this.successMessage = '', 3000);
      },
      error: () => { this.errorMessage = 'Failed to cancel order.'; }
    });
  }

  prevPage(): void { if (this.currentPage > 0) this.loadOrders(this.currentPage - 1); }
  nextPage(): void { if (this.currentPage < this.totalPages - 1) this.loadOrders(this.currentPage + 1); }
}
