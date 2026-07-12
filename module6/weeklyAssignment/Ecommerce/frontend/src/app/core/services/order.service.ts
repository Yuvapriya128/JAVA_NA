import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { ApiClientService } from '../http/api-client.service';
import { API_ENDPOINTS } from '../http/endpoints';
import { environment } from '../../../environments/environment';
import { throwError } from 'rxjs';
import { BaseCrudService } from './base.service';
import {
  OrderRequestDTO,
  OrderResponseDTO,
  OrderUpdateDTO
} from '../dto/order/order.dto';

@Injectable({
  providedIn: 'root'
})
export class OrderService extends BaseCrudService<OrderResponseDTO> {
  private endpoint = API_ENDPOINTS.ORDER;

  constructor(protected override apiClient: ApiClientService) {
    super(apiClient);
  }

  getAllOrders(): Observable<OrderResponseDTO[]> {
    return this.apiClient.get<any>(this.endpoint).pipe(
      map(response => this.extractArray(response)),
      tap(data => console.log('✓ Orders loaded:', data.length, 'records'))
    );
  }

  getOrderById(id: number): Observable<OrderResponseDTO> {
    const url = `${this.endpoint}/${id}`;
    console.log('[OrderService] GET URL:', url);
    console.log('[OrderService] Request ID:', id);
    return this.apiClient.get<OrderResponseDTO>(url);
  }

  createOrder(data: OrderRequestDTO): Observable<OrderResponseDTO> {
    console.log('=== CHECKOUT REQUEST ===');
    console.log('URL:', `${environment.apiUrl}${this.endpoint}`);
    console.log('Endpoint:', this.endpoint);
    console.log('Request Payload:', data);
    console.log('Request Body Keys:', Object.keys(data));

    return this.apiClient.post<OrderResponseDTO>(this.endpoint, data).pipe(
      tap(() => {
        console.log('✓ Order created successfully');
      }),
      catchError((error) => {
        console.error('✗ Order creation failed with status:', error.status);
        console.error('Error response body:', error.error);
        console.error('Error message:', error.message);
        return throwError(() => error);
      })
    );
  }

  updateOrder(id: number, data: OrderUpdateDTO): Observable<OrderResponseDTO> {
    return this.apiClient.put<OrderResponseDTO>(`${this.endpoint}/${id}`, data);
  }

  deleteOrder(id: number): Observable<any> {
    return this.apiClient.delete(`${this.endpoint}/${id}`);
  }

  getOrdersByCustomer(customerId: number): Observable<OrderResponseDTO[]> {
    return this.apiClient.get<OrderResponseDTO[]>(`${this.endpoint}/customer/${customerId}`);
  }
}

