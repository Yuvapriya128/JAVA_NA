import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { CustomerService } from '../../../services/customer/customer.service';

import { CustomerDetailsComponent } from './customer-details.component';

describe('CustomerDetailsComponent', () => {
  let component: CustomerDetailsComponent;
  let fixture: ComponentFixture<CustomerDetailsComponent>;

  beforeEach(async () => {
    const customerServiceMock = {
      getCustomer: () => of({
        customerId: 101,
        customerName: 'Test',
        email: 'test@example.com',
        phoneNumber: '9999999999',
        city: 'City',
        creditScore: 700,
        role: 'USER',
        active: true
      })
    };

    await TestBed.configureTestingModule({
      imports: [CustomerDetailsComponent],
      providers: [
        provideRouter([]),
        { provide: CustomerService, useValue: customerServiceMock },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: convertToParamMap({})
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(CustomerDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
