import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Page } from '../models/page.model';

export interface AdminStats {
  totalUsers: number;
  totalBuyers: number;
  totalSellers: number;
  activeSellers: number;
  totalRFQs: number;
  totalQuotes: number;
  totalOrdersCompleted: number;
  totalGMV: number;
  totalCommissionRevenue: number;
  rfqToOrderConversionRate: number;
}

export interface AdminUser {
  id: number;
  name: string;
  email: string;
  phone?: string;
  role: 'BUYER' | 'SELLER' | 'ADMIN';
  status: 'ACTIVE' | 'SUSPENDED' | 'DEACTIVATED';
  createdAt: string;
}

export interface AdminDispute {
  id: number;
  orderId: number;
  complainantId: number;
  complainantName: string;
  reason: string;
  status: 'OPEN' | 'RESOLVED' | 'CLOSED';
  resolution?: string;
  createdAt: string;
  resolvedAt?: string;
}

@Injectable({ providedIn: 'root' })
export class AdminService {
  private apiUrl = `${environment.apiUrl}/admin`;

  constructor(private http: HttpClient) {}

  // Analytics
  getStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/stats`);
  }

  // User Management
  getUsers(role?: string, search?: string, page = 0, size = 20): Observable<Page<AdminUser>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (role) params = params.set('role', role);
    if (search) params = params.set('search', search);
    return this.http.get<Page<AdminUser>>(`${this.apiUrl}/users`, { params });
  }

  getUserById(id: number): Observable<AdminUser> {
    return this.http.get<AdminUser>(`${this.apiUrl}/users/${id}`);
  }

  updateUserStatus(id: number, status: string): Observable<AdminUser> {
    return this.http.put<AdminUser>(`${this.apiUrl}/users/${id}/status`, { status });
  }

  deactivateUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/${id}`);
  }

  resetUserPassword(id: number): Observable<AdminUser> {
    return this.http.post<AdminUser>(`${this.apiUrl}/users/${id}/reset-password`, {});
  }

  // Seller Verification
  getPendingSellerVerifications(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/sellers/pending`, { params });
  }

  approveSeller(userId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/sellers/${userId}/approve`, {});
  }

  rejectSeller(userId: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/sellers/${userId}/reject`, {});
  }

  // RFQ Monitoring
  getAllRFQs(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/rfqs`, { params });
  }

  closeRFQ(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/rfqs/${id}/close`, {});
  }

  deleteRFQ(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/rfqs/${id}`);
  }

  // Quote Monitoring
  getAllQuotes(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/quotes`, { params });
  }

  // Order Management
  getAllOrders(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/orders`, { params });
  }

  updateOrderStatus(id: number, status: string): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/orders/${id}/status?status=${status}`, {});
  }

  cancelOrder(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/orders/${id}/cancel`, {});
  }

  // Payment & Commission
  getPlatformRevenue(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.apiUrl}/revenue`);
  }

  getTransactionHistory(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/transactions`, { params });
  }

  getPayoutReports(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/payouts`, { params });
  }

  // Dispute Resolution
  getDisputes(page = 0, size = 20): Observable<Page<AdminDispute>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<AdminDispute>>(`${this.apiUrl}/disputes`, { params });
  }

  resolveDispute(id: number, resolution: string): Observable<AdminDispute> {
    return this.http.put<AdminDispute>(`${this.apiUrl}/disputes/${id}/resolve`, { resolution });
  }

  // Reviews
  getAllReviews(page = 0, size = 20): Observable<Page<any>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<any>>(`${this.apiUrl}/reviews`, { params });
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/reviews/${id}`);
  }

  // Notifications
  broadcastNotification(type: string, message: string, targetRole?: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/notifications`, { type, message, targetRole });
  }
}
