import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { BuyerStats, BuyerProfile, BuyerProfileRequest, Vehicle, VehicleRequest } from '../models/buyer.model';

@Injectable({ providedIn: 'root' })
export class BuyerService {
  private apiUrl = `${environment.apiUrl}/buyer`;

  constructor(private http: HttpClient) {}

  getDashboardStats(): Observable<BuyerStats> {
    return this.http.get<BuyerStats>(`${this.apiUrl}/dashboard`);
  }

  getProfile(): Observable<BuyerProfile> {
    return this.http.get<BuyerProfile>(`${this.apiUrl}/profile`);
  }

  updateProfile(request: BuyerProfileRequest): Observable<BuyerProfile> {
    return this.http.put<BuyerProfile>(`${this.apiUrl}/profile`, request);
  }

  getVehicles(): Observable<Vehicle[]> {
    return this.http.get<Vehicle[]>(`${this.apiUrl}/vehicles`);
  }

  addVehicle(request: VehicleRequest): Observable<Vehicle> {
    return this.http.post<Vehicle>(`${this.apiUrl}/vehicles`, request);
  }

  deleteVehicle(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/vehicles/${id}`);
  }
}
