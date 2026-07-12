import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlightItem } from './flight-item';

describe('FlightItem', () => {
  let component: FlightItem;
  let fixture: ComponentFixture<FlightItem>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlightItem],
    }).compileComponents();

    fixture = TestBed.createComponent(FlightItem);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
