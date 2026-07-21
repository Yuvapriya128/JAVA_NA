import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, catchError } from 'rxjs';
import {
  CreateLoanApplicationRequest,
  CreateLoanRequest,
  EmiCalculationRequest,
  EmiCalculationResponse,
  EmiPaymentHistory,
  EmiPaymentReceiptDTO,
  EmiSchedule,
  EmiPaymentRequest,
  EmiPaymentResponse,
  LoanApplicationDTO,
  LoanDashboard,
  LoanProduct,
  LoanSummary,
  UpdateLoanApplicationStatusRequest
} from '../../dto/loan/loan.dto';
import { environment } from '../../../environments/environment';
import { PageResponse } from '../shared/page-response';
import { handleHttpError } from '../shared/http-error-handler';
export type {
  CreateLoanApplicationRequest,
  CreateLoanRequest,
  EmiCalculationRequest,
  EmiCalculationResponse,
  EmiPaymentHistory,
  EmiPaymentReceiptDTO,
  EmiSchedule,
  EmiPaymentRequest,
  EmiPaymentResponse,
  LoanApplicationDTO,
  LoanDashboard,
  LoanProduct,
  LoanSummary,
  UpdateLoanApplicationStatusRequest
} from '../../dto/loan/loan.dto';

@Injectable({
  providedIn: 'root'
})
export class LoanService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Fetches available loan products.
   */
  getLoanProducts(): Observable<LoanProduct[]> {
    return this.http
      .get<LoanProduct[]>(`${this.baseUrl}/loan-products`)
      .pipe(catchError(handleHttpError('Get loan products request')));
  }

  /**
   * Creates a new loan and EMI schedule.
   */
  createLoan(request: CreateLoanRequest): Observable<LoanSummary> {
    return this.http
      .post<LoanSummary>(`${this.baseUrl}/loans`, request)
      .pipe(catchError(handleHttpError('Create loan request')));
  }

  /**
   * Fetches paginated loans.
   */
  getLoans(page = 0, size = 10): Observable<PageResponse<LoanSummary>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http
      .get<PageResponse<LoanSummary>>(`${this.baseUrl}/loans`, { params })
      .pipe(catchError(handleHttpError('Get loans request')));
  }

  /**
   * Fetches loan details by ID.
   */
  getLoan(loanId: number): Observable<LoanSummary> {
    return this.http
      .get<LoanSummary>(`${this.baseUrl}/loans/${loanId}`)
      .pipe(catchError(handleHttpError('Get loan request')));
  }

  /**
   * Fetches loan dashboard metrics.
   */
  getDashboard(): Observable<LoanDashboard> {
    return this.http
      .get<LoanDashboard>(`${this.baseUrl}/dashboard`)
      .pipe(catchError(handleHttpError('Get loan dashboard request')));
  }

  /**
   * Bulk updates interest rates for loan types.
   */
  reviseInterestRates(loanTypes: string[], annualInterestRate: number): Observable<number> {
    let params = new HttpParams().set('annualInterestRate', String(annualInterestRate));
    loanTypes.forEach((loanType) => {
      params = params.append('loanTypes', loanType);
    });

    return this.http
      .patch<number>(`${this.baseUrl}/loans/interest-rate`, null, { params })
      .pipe(catchError(handleHttpError('Revise interest rates request')));
  }

  /**
   * Updates interest rate for a specific loan.
   */
  updateLoanInterest(loanId: number, rate: number): Observable<LoanSummary> {
    const params = new HttpParams().set('rate', String(rate));
    return this.http
      .put<LoanSummary>(`${this.baseUrl}/loans/${loanId}/interest`, null, { params })
      .pipe(catchError(handleHttpError('Update loan interest request')));
  }

  /**
   * Deletes a loan by ID.
   */
  deleteLoan(loanId: number): Observable<void> {
    return this.http
      .delete<void>(`${this.baseUrl}/loans/${loanId}`)
      .pipe(catchError(handleHttpError('Delete loan request')));
  }

  /**
   * Pays an EMI installment.
   */
  payEmi(request: EmiPaymentRequest): Observable<EmiPaymentResponse> {
    return this.http
      .post<EmiPaymentResponse>(`${this.baseUrl}/emis/pay`, request)
      .pipe(catchError(handleHttpError('Pay EMI request')));
  }

  /**
   * Fetches EMI payment history with pagination.
   */
  getEmiPayments(page = 0, size = 10): Observable<EmiPaymentHistory[] | PageResponse<EmiPaymentHistory>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http
      .get<EmiPaymentHistory[] | PageResponse<EmiPaymentHistory>>(`${this.baseUrl}/emis/payments`, { params })
      .pipe(catchError(handleHttpError('Get EMI payments request')));
  }

  /**
   * Applies for a loan product.
   */
  applyForLoan(request: CreateLoanApplicationRequest): Observable<LoanApplicationDTO> {
    return this.http
      .post<LoanApplicationDTO>(`${this.baseUrl}/loan-products/apply`, request)
      .pipe(catchError(handleHttpError('Apply for loan request')));
  }

  /**
   * Gets payment receipt for an EMI.
   */
  getPaymentReceipt(emiId: number): Observable<EmiPaymentReceiptDTO> {
    return this.http
      .get<EmiPaymentReceiptDTO>(`${this.baseUrl}/emis/payments/${emiId}/receipt`)
      .pipe(catchError(handleHttpError('Get payment receipt request')));
  }

  /**
   * Gets EMI schedules for a specific loan.
   */
  getEmisByLoan(loanId: number, page = 0, size = 100): Observable<PageResponse<EmiSchedule>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http
      .get<PageResponse<EmiSchedule>>(`${this.baseUrl}/emis/by-loan/${loanId}`, { params })
      .pipe(catchError(handleHttpError('Get loan EMIs request')));
  }

  /**
   * Gets my loan applications.
   */
  getMyLoanApplications(page = 0, size = 10): Observable<PageResponse<LoanApplicationDTO>> {
    const params = new HttpParams().set('page', String(page)).set('size', String(size));
    return this.http
      .get<PageResponse<LoanApplicationDTO>>(`${this.baseUrl}/loan-products/my-applications`, { params })
      .pipe(catchError(handleHttpError('Get my loan applications request')));
  }

  /**
   * Gets all loan applications for manager/admin review.
   */
  getLoanApplications(page = 0, size = 50, status?: string): Observable<PageResponse<LoanApplicationDTO>> {
    let params = new HttpParams().set('page', String(page)).set('size', String(size));
    if (status && status !== 'ALL') {
      params = params.set('status', status);
    }

    return this.http
      .get<PageResponse<LoanApplicationDTO>>(`${this.baseUrl}/loan-products/applications`, { params })
      .pipe(catchError(handleHttpError('Get loan applications request')));
  }

  /**
   * Updates a loan application status for review actions.
   */
  updateLoanApplicationStatus(applicationId: number, request: UpdateLoanApplicationStatusRequest): Observable<LoanApplicationDTO> {
    return this.http
      .patch<LoanApplicationDTO>(`${this.baseUrl}/loan-products/applications/${applicationId}/status`, request)
      .pipe(catchError(handleHttpError('Update loan application status request')));
  }

  /**
   * Calculates EMI without creating a loan.
   * Returns EMI amount, total interest, and total payment.
   */
  calculateEmi(request: EmiCalculationRequest): Observable<EmiCalculationResponse> {
    return this.http
      .post<EmiCalculationResponse>(`${this.baseUrl}/loans/calculate-emi`, request)
      .pipe(catchError(handleHttpError('Calculate EMI request')));
  }
}
