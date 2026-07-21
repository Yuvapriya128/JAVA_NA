import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { LoanProductsComponent } from './loan-products.component';

describe('LoanProductsComponent', () => {
  let component: LoanProductsComponent;
  let fixture: ComponentFixture<LoanProductsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoanProductsComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              routeConfig: {
                path: 'loan-products'
              },
              queryParamMap: {
                get: () => null
              }
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(LoanProductsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
