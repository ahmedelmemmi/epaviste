import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Quote, QuoteRequest, UpdateQuoteRequest } from '../models/quote.model';

@Injectable({ providedIn: 'root' })
export class QuoteService {
  private apiUrl = `${environment.apiUrl}/quotes`;
  constructor(private http: HttpClient) {}

  submitQuote(request: QuoteRequest): Observable<Quote> {
    return this.http.post<Quote>(this.apiUrl, request);
  }

  getQuotesForRFQ(rfqId: number): Observable<Quote[]> {
    return this.http.get<Quote[]>(`${this.apiUrl}/rfq/${rfqId}`);
  }

  getMyQuotes(): Observable<Quote[]> {
    return this.http.get<Quote[]>(`${this.apiUrl}/my`);
  }

  acceptQuote(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/accept`, {});
  }

  rejectQuote(id: number): Observable<any> {
    return this.http.put<any>(`${this.apiUrl}/${id}/reject`, {});
  }

  updateQuote(id: number, request: UpdateQuoteRequest): Observable<Quote> {
    return this.http.put<Quote>(`${this.apiUrl}/${id}`, request);
  }

  withdrawQuote(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
