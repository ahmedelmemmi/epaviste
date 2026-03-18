import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { SellerStats, SellerEarnings, SellerProfile, SellerProfileRequest, SellerPublicProfile } from '../models/seller.model';
import { RFQ } from '../models/rfq.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class SellerService {
  private apiUrl = `${environment.apiUrl}/seller`;

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<SellerStats> {
    return this.http.get<SellerStats>(`${this.apiUrl}/dashboard`);
  }

  getEarnings(): Observable<SellerEarnings> {
    return this.http.get<SellerEarnings>(`${this.apiUrl}/earnings`);
  }

  getProfile(): Observable<SellerProfile> {
    return this.http.get<SellerProfile>(`${this.apiUrl}/profile`);
  }

  updateProfile(request: SellerProfileRequest): Observable<SellerProfile> {
    return this.http.put<SellerProfile>(`${this.apiUrl}/profile`, request);
  }

  getSellerRFQs(
    carBrand?: string,
    carModel?: string,
    partCategory?: string,
    location?: string,
    page = 0,
    size = 10
  ): Observable<Page<RFQ>> {
    let params = new HttpParams().set('page', page).set('size', size);
    if (carBrand) params = params.set('carBrand', carBrand);
    if (carModel) params = params.set('carModel', carModel);
    if (partCategory) params = params.set('partCategory', partCategory);
    if (location) params = params.set('location', location);
    return this.http.get<Page<RFQ>>(`${this.apiUrl}/rfqs`, { params });
  }

  getSellerPublicProfile(sellerId: number): Observable<SellerPublicProfile> {
    return this.http.get<SellerPublicProfile>(`${this.apiUrl}/${sellerId}/public-profile`);
  }
}
