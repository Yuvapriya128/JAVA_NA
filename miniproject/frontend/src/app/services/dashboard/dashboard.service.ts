import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import { environment } from '../../../environments/environment';
import { handleHttpError } from '../shared/http-error-handler';

export interface LoanDashboard {
  totalCustomers: number;
  totalLoans: number;
  activeLoans: number;
  closedLoans: number;
  overdueEMIs: number;
  totalEMICollected: number;
  totalPenaltyCollected: number;
  averageInterestRate: number;
  highestOutstandingLoan: string;
  highestPayingCustomer: string;
  NPAAccounts: number;
}

export interface EmiInsights {
  highestEmi: number;
  lowestEmi: number;
  averageEmi: number;
  totalMonthlyEmiCollection: number;
  upcomingEmiAmount: number;
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Fetches top-level dashboard metrics.
   */
  getDashboard(): Observable<LoanDashboard> {
    return this.http
      .get<LoanDashboard>(`${this.baseUrl}/dashboard`)
      .pipe(catchError(handleHttpError('Get dashboard request')));
  }

  /**
   * Fetches admin dashboard metrics.
   */
  getAdminDashboard(): Observable<LoanDashboard> {
    return this.http
      .get<LoanDashboard>(`${this.baseUrl}/dashboard/admin`)
      .pipe(catchError(handleHttpError('Get admin dashboard request')));
  }

  /**
   * Fetches EMI insight metrics for dashboard cards.
   */
  getEmiInsights(): Observable<EmiInsights> {
    return this.http
      .get<EmiInsights>(`${this.baseUrl}/dashboard/emi-insights`)
      .pipe(catchError(handleHttpError('Get EMI insights request')));
  }
}
