import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { CustomerService } from '../../../services/customer/customer.service';

import { CustomerListComponent } from './customer-list.component';

describe('CustomerListComponent', () => {
  let component: CustomerListComponent;
  let fixture: ComponentFixture<CustomerListComponent>;

  beforeEach(async () => {
    const customerServiceMock: Partial<CustomerService> = {
      getAllCustomers: () => of({
        content: [],
        totalElements: 0,
        totalPages: 1,
        number: 0,
        size: 10,
        first: true,
        last: true,
        numberOfElements: 0,
        empty: true
      }),
      deactivateCustomer: () => of({
        customerId: 0,
        customerName: '',
        email: '',
        phoneNumber: '',
        city: '',
        creditScore: 0,
        role: 'USER',
        active: false
      })
    };

    await TestBed.configureTestingModule({
      imports: [CustomerListComponent],
      providers: [
        provideRouter([]),
        { provide: CustomerService, useValue: customerServiceMock }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
