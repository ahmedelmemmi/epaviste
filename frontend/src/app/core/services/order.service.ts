import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Order } from '../models/order.model';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = `${environment.apiUrl}/orders`;
  constructor(private http: HttpClient) {}

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/my`);
  }

  getOrder(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/${id}`);
  }

  confirmDelivery(id: number): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${id}/confirm-delivery`, {});
  }

  markShipped(id: number): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${id}/ship`, {});
  }

  updateOrderStatus(id: number, status: string): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${id}/status`, { status });
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/${id}/cancel`, {});
  }
}
