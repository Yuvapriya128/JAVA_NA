import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';
import { LoanService } from '../../../services/loan/loan.service';

import { PaymentHistoryComponent } from './payment-history.component';

describe('PaymentHistoryComponent', () => {
  let component: PaymentHistoryComponent;
  let fixture: ComponentFixture<PaymentHistoryComponent>;

  beforeEach(async () => {
    const loanServiceMock = {
      getEmiPayments: () => of([])
    };

    await TestBed.configureTestingModule({
      imports: [PaymentHistoryComponent],
      providers: [{ provide: LoanService, useValue: loanServiceMock }]
    }).compileComponents();

    fixture = TestBed.createComponent(PaymentHistoryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
