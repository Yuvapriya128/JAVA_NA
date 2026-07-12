import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IncrementBy } from './increment-by';

describe('IncrementBy', () => {
  let component: IncrementBy;
  let fixture: ComponentFixture<IncrementBy>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IncrementBy],
    }).compileComponents();

    fixture = TestBed.createComponent(IncrementBy);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
