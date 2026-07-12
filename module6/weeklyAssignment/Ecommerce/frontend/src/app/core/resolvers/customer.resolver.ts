import { Injectable } from '@angular/core';
import { ResolveFn } from '@angular/router';
import { CustomerService } from '../services/customer.service';

@Injectable({
  providedIn: 'root'
})
export class CustomerResolver {
  constructor(private customerService: CustomerService) {}

  resolve(route: any) {
    const id = route.paramMap.get('id');
    return this.customerService.getCustomerById(id);
  }
}

export const customerResolver: ResolveFn<any> = (route, state) => {
  return null;
};

