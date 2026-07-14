import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map, tap } from 'rxjs/operators';
import { ApiClientService } from '../http/api-client.service';
import { API_ENDPOINTS } from '../http/endpoints';
import { BaseCrudService } from './base.service';
import {
  ProductRequestDTO,
  ProductResponseDTO,
  ProductUpdateDTO
} from '../dto/product/product.dto';

@Injectable({
  providedIn: 'root'
})
export class ProductService extends BaseCrudService<ProductResponseDTO> {
  private endpoint = API_ENDPOINTS.PRODUCT;

  constructor(protected override apiClient: ApiClientService) {
    super(apiClient);
  }

  getAllProducts(): Observable<ProductResponseDTO[]> {
    return this.apiClient.get<any>(this.endpoint).pipe(
      map(response => this.extractArray(response)),
      tap(data => console.log('✓ Products loaded:', data.length, 'records'))
    );
  }

  getProductById(id: number): Observable<ProductResponseDTO> {
    const url = `${this.endpoint}/${id}`;
    console.log('[ProductService] GET URL:', url);
    console.log('[ProductService] Request ID:', id);
    return this.apiClient.get<ProductResponseDTO>(url);
  }

  createProduct(data: ProductRequestDTO): Observable<ProductResponseDTO> {
    return this.apiClient.post<ProductResponseDTO>(this.endpoint, data);
  }

  updateProduct(id: number, data: ProductUpdateDTO): Observable<ProductResponseDTO> {
    return this.apiClient.put<ProductResponseDTO>(`${this.endpoint}/${id}`, data);
  }

  deleteProduct(id: number): Observable<any> {
    return this.apiClient.delete(`${this.endpoint}/${id}`);
  }

  getProductsByCategory(category: string): Observable<ProductResponseDTO[]> {
    return this.apiClient.get<ProductResponseDTO[]>(`${this.endpoint}/category/${category}`);
  }

  getProductsByBrand(brand: string): Observable<ProductResponseDTO[]> {
    return this.apiClient.get<ProductResponseDTO[]>(`${this.endpoint}/brand/${brand}`);
  }

  getProductsByName(name: string): Observable<ProductResponseDTO[]> {
    return this.apiClient.get<ProductResponseDTO[]>(`${this.endpoint}/name/${name}`);
  }
}

