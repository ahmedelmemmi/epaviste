import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { RFQ, RFQRequest } from '../models/rfq.model';
import { Page } from '../models/page.model';

@Injectable({ providedIn: 'root' })
export class RfqService {
  private apiUrl = `${environment.apiUrl}/rfqs`;
  constructor(private http: HttpClient) {}

  createRFQ(request: RFQRequest): Observable<RFQ> {
    return this.http.post<RFQ>(this.apiUrl, request);
  }

  getOpenRFQs(page = 0, size = 10): Observable<Page<RFQ>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<RFQ>>(this.apiUrl, { params });
  }

  getMyRFQs(): Observable<RFQ[]> {
    return this.http.get<RFQ[]>(`${this.apiUrl}/my`);
  }

  getRFQ(id: number): Observable<RFQ> {
    return this.http.get<RFQ>(`${this.apiUrl}/${id}`);
  }

  cancelRFQ(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
