import { Injectable } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { catchError, map, tap } from 'rxjs/operators';
import { ApiClientService } from '../http/api-client.service';
import { API_ENDPOINTS } from '../http/endpoints';
import { BaseCrudService } from './base.service';
import {
  CustomerRequestDTO,
  CustomerResponseDTO,
  CustomerUpdateDTO,
  AdminCustomerRequestDTO,
  CustomerMeResponseDTO,
  CustomerMeUpdateDTO,
  CustomerPasswordChangeDTO
} from '../dto/customer/customer.dto';

@Injectable({
  providedIn: 'root'
})
export class CustomerService extends BaseCrudService<CustomerResponseDTO> {
  private endpoint = API_ENDPOINTS.CUSTOMER;

  constructor(protected override apiClient: ApiClientService) {
    super(apiClient);
  }

  getAllCustomers(): Observable<CustomerResponseDTO[]> {
    return this.apiClient.get<any>(this.endpoint).pipe(
      map(response => this.extractArray(response)),
      tap(data => console.log('✓ Customers loaded:', data.length, 'records'))
    );
  }

  getCustomerById(id: number): Observable<CustomerResponseDTO> {
    const url = `${this.endpoint}/${id}`;
    console.log('[CustomerService] GET URL:', url);
    console.log('[CustomerService] Request ID:', id);
    return this.apiClient.get<CustomerResponseDTO>(url);
  }

  createCustomer(data: CustomerRequestDTO): Observable<CustomerResponseDTO> {
    return this.apiClient.post<CustomerResponseDTO>(this.endpoint, data);
  }

  register(data: CustomerRequestDTO): Observable<CustomerResponseDTO> {
    return this.apiClient.post<CustomerResponseDTO>(this.endpoint, data);
  }

  createCustomerByAdmin(data: AdminCustomerRequestDTO): Observable<CustomerResponseDTO> {
    return this.apiClient.post<CustomerResponseDTO>(`${this.endpoint}/admin/create`, data);
  }

  updateCustomer(id: number, data: CustomerUpdateDTO): Observable<CustomerResponseDTO> {
    return this.apiClient.put<CustomerResponseDTO>(`${this.endpoint}/${id}`, data);
  }

  deleteCustomer(id: number): Observable<any> {
    return this.apiClient.delete(`${this.endpoint}/${id}`);
  }

  changeRole(id: number, data: { role: string }): Observable<CustomerResponseDTO> {
    return this.apiClient.put<CustomerResponseDTO>(`${this.endpoint}/${id}/role`, data);
  }

  getCustomerOrders(id: number): Observable<any[]> {
    return this.apiClient.get<any[]>(`${this.endpoint}/${id}/orders`);
  }

  getMyProfile(): Observable<CustomerMeResponseDTO> {
    const url = `${this.endpoint}/me`;
    console.log('[CustomerService] GET me URL:', url);
    return this.apiClient.get<CustomerMeResponseDTO>(url).pipe(
      tap((response) => {
        console.log('[CustomerService] GET me response:', response);
      })
    );
  }

  updateMyProfile(data: CustomerMeUpdateDTO): Observable<CustomerMeResponseDTO> {
    return this.apiClient.put<CustomerMeResponseDTO>(`${this.endpoint}/me`, data);
  }

  changeMyPassword(data: CustomerPasswordChangeDTO): Observable<void> {
    const endpoint = `${this.endpoint}/me/password`;
    const currentPassword = data.currentPassword;
    const newPassword = data.newPassword;
    const confirmPassword = data.confirmPassword || data.newPassword;

    const payloadCandidates: Array<Record<string, string>> = [
      { currentPassword, newPassword },
      { oldPassword: currentPassword, newPassword },
      { currentPassword, newPassword, confirmPassword },
      { oldPassword: currentPassword, newPassword, confirmPassword }
    ];

    const tryPayload = (index: number): Observable<void> => {
      const payload = payloadCandidates[index];
      console.log('[CustomerService] changeMyPassword attempt', index + 1, payload);

      return this.apiClient.put<void>(endpoint, payload).pipe(
        catchError((error) => {
          const isRetryable400 = error?.status === 400 && index < payloadCandidates.length - 1;
          if (isRetryable400) {
            return tryPayload(index + 1);
          }

          return throwError(() => error);
        })
      );
    };

    return tryPayload(0);
  }
}
