import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/reviews`;
  constructor(private http: HttpClient) {}

  submitReview(request: { orderId: number; rating: number; comment: string }): Observable<any> {
    return this.http.post<any>(this.apiUrl, request);
  }

  getSellerReviews(sellerId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/seller/${sellerId}`);
  }
}
