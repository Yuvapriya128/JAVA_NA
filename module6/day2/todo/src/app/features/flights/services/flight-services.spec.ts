import { TestBed } from '@angular/core/testing';

import { FlightServices } from './flight-services';

describe('FlightServices', () => {
  let service: FlightServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlightServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
