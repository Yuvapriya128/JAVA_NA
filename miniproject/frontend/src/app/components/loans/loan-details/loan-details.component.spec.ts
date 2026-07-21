import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap, provideRouter } from '@angular/router';
import { of } from 'rxjs';
import { LoanService } from '../../../services/loan/loan.service';

import { LoanDetailsComponent } from './loan-details.component';

describe('LoanDetailsComponent', () => {
  let component: LoanDetailsComponent;
  let fixture: ComponentFixture<LoanDetailsComponent>;

  beforeEach(async () => {
    const loanServiceMock = {
      getLoan: () => of({
        loanId: 1001,
        loanType: 'HOME_LOAN',
        principalAmount: 100000,
        annualInterestRate: 8.5,
        tenureMonths: 60,
        emiAmount: 2000,
        loanStatus: 'ACTIVE',
        customerId: 101,
        customerName: 'Test',
        city: 'City'
      }),
      deleteLoan: () => of(void 0)
    };

    await TestBed.configureTestingModule({
      imports: [LoanDetailsComponent],
      providers: [
        provideRouter([]),
        { provide: LoanService, useValue: loanServiceMock },
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

    fixture = TestBed.createComponent(LoanDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
