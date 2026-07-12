import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DecrementBy } from './decrement-by';

describe('DecrementBy', () => {
  let component: DecrementBy;
  let fixture: ComponentFixture<DecrementBy>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DecrementBy],
    }).compileComponents();

    fixture = TestBed.createComponent(DecrementBy);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
