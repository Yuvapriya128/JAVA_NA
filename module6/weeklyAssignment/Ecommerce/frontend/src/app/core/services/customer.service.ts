import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiClientService } from '../http/api-client.service';
import { API_ENDPOINTS } from '../http/endpoints';
import { BaseCrudService } from './base.service';
import {
  CustomerRequestDTO,
  CustomerResponseDTO,
  CustomerUpdateDTO,
  AdminCustomerRequestDTO
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
}

