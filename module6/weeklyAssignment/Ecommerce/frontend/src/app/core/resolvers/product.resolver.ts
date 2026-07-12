import { Injectable } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { ProductService } from '../services/product.service';

@Injectable({
  providedIn: 'root'
})
export class ProductResolver {
  constructor(private productService: ProductService) {}

  resolve(route: any) {
    const id = route.paramMap.get('id');
    return this.productService.getProductById(id);
  }
}

export const productResolver: ResolveFn<any> = (route, state) => {
  return null;
};

