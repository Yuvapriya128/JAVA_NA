import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, provideRouter } from '@angular/router';
import { convertToParamMap } from '@angular/router';

import { InterestManagementComponent } from './interest-management.component';

describe('InterestManagementComponent', () => {
  let component: InterestManagementComponent;
  let fixture: ComponentFixture<InterestManagementComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InterestManagementComponent],
      providers: [
        provideRouter([]),
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

    fixture = TestBed.createComponent(InterestManagementComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
