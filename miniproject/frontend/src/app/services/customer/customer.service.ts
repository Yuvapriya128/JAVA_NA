import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable, catchError, throwError } from 'rxjs';
import { CreateCustomerRequest, CustomerResponse, UpdateCustomerRequest } from '../../dto/customer/customer.dto';
import { environment } from '../../../environments/environment';
import { handleHttpError } from '../shared/http-error-handler';
import { PageResponse } from '../shared/page-response';
export type { CreateCustomerRequest, CustomerResponse, UpdateCustomerRequest } from '../../dto/customer/customer.dto';

@Injectable({
  providedIn: 'root'
})
export class CustomerService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = environment.apiUrl;

  /**
   * Creates a new customer.
   */
  createCustomer(request: CreateCustomerRequest): Observable<CustomerResponse> {
    return this.http
      .post<CustomerResponse>(`${this.baseUrl}/customers`, request)
      .pipe(catchError(handleHttpError('Create customer request')));
  }

  /**
   * Fetches a customer by ID.
   */
  getCustomer(customerId: number): Observable<CustomerResponse> {
    return this.http
      .get<CustomerResponse>(`${this.baseUrl}/customers/${customerId}`)
      .pipe(catchError(handleHttpError('Get customer request')));
  }

  /**
   * Fetches all customers with pagination.
   */
  getAllCustomers(page = 0, size = 10, sort = 'customerId', direction = 'ASC'): Observable<PageResponse<CustomerResponse>> {
    const params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', sort)
      .set('direction', direction);
    return this.http
      .get<PageResponse<CustomerResponse>>(`${this.baseUrl}/customers`, { params })
      .pipe(catchError(handleHttpError('Get all customers request')));
  }

  /**
   * Deactivates a customer by ID.
   */
  deactivateCustomer(customerId: number): Observable<CustomerResponse> {
    return this.http
      .patch<CustomerResponse>(`${this.baseUrl}/customers/${customerId}/deactivate`, {})
      .pipe(catchError(handleHttpError('Deactivate customer request')));
  }

  /**
   * Approves/activates a customer by ID.
   */
  approveCustomer(customerId: number): Observable<CustomerResponse> {
    return this.http
      .patch<CustomerResponse>(`${this.baseUrl}/customers/${customerId}/activate`, {})
      .pipe(catchError(handleHttpError('Approve customer request')));
  }

  /**
   * Updates customer details by ID.
   */
  updateCustomer(customerId: number, request: UpdateCustomerRequest): Observable<CustomerResponse> {
    const url = `${this.baseUrl}/customers/${customerId}`;
    return this.http
      .put<CustomerResponse>(url, request)
      .pipe(
        catchError((error) => {
          if (error?.status === 404 || error?.status === 405) {
            return this.http.patch<CustomerResponse>(url, request);
          }
          return throwError(() => error);
        })
      )
      .pipe(catchError(handleHttpError('Update customer request')));
  }

  /**
   * Searches customers with filters.
   */
  searchCustomers(
    name?: string,
    email?: string,
    phone?: string,
    city?: string,
    role?: string,
    active?: boolean,
    creditScoreMin?: number,
    creditScoreMax?: number,
    page = 0,
    size = 10,
    sort = 'customerId',
    direction = 'ASC'
  ): Observable<PageResponse<CustomerResponse>> {
    let params = new HttpParams()
      .set('page', String(page))
      .set('size', String(size))
      .set('sort', sort)
      .set('direction', direction);

    if (name) params = params.set('name', name);
    if (email) params = params.set('email', email);
    if (phone) params = params.set('phone', phone);
    if (city) params = params.set('city', city);
    if (role) params = params.set('role', role);
    if (active !== undefined) params = params.set('active', String(active));
    if (creditScoreMin !== undefined) params = params.set('creditScoreMin', String(creditScoreMin));
    if (creditScoreMax !== undefined) params = params.set('creditScoreMax', String(creditScoreMax));

    return this.http
      .get<PageResponse<CustomerResponse>>(`${this.baseUrl}/customers/search`, { params })
      .pipe(catchError(handleHttpError('Search customers request')));
  }
}
