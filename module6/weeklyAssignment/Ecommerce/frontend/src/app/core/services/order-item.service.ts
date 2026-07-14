import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiClientService } from '../http/api-client.service';
import { API_ENDPOINTS } from '../http/endpoints';
import { BaseCrudService } from './base.service';
import {
  OrderItemRequestDTO,
  OrderItemResponseDTO,
  OrderItemUpdateDTO
} from '../dto/order-item/orderitem.dto';

@Injectable({
  providedIn: 'root'
})
export class OrderItemService extends BaseCrudService<OrderItemResponseDTO> {
  private endpoint = API_ENDPOINTS.ORDER_ITEM;

  constructor(protected override apiClient: ApiClientService) {
    super(apiClient);
  }

  getAllOrderItems(): Observable<OrderItemResponseDTO[]> {
    return this.apiClient.get<any>(this.endpoint).pipe(
      map(response => this.extractArray(response)),
      tap(data => console.log('✓ OrderItems loaded:', data.length, 'records'))
    );
  }

  getOrderItemById(id: number): Observable<OrderItemResponseDTO> {
    const url = `${this.endpoint}/${id}`;
    console.log('[OrderItemService] GET URL:', url);
    console.log('[OrderItemService] Request ID:', id);
    return this.apiClient.get<OrderItemResponseDTO>(url);
  }

  createOrderItem(data: OrderItemRequestDTO): Observable<OrderItemResponseDTO> {
    return this.apiClient.post<OrderItemResponseDTO>(this.endpoint, data);
  }

  updateOrderItem(id: number, data: OrderItemUpdateDTO): Observable<OrderItemResponseDTO> {
    return this.apiClient.put<OrderItemResponseDTO>(`${this.endpoint}/${id}`, data);
  }

  deleteOrderItem(id: number): Observable<any> {
    return this.apiClient.delete(`${this.endpoint}/${id}`);
  }

  getOrderItemsByOrder(orderId: number): Observable<OrderItemResponseDTO[]> {
    return this.apiClient.get<OrderItemResponseDTO[]>(`${this.endpoint}/order/${orderId}`);
  }
}

