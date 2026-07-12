import { TestBed } from '@angular/core/testing';

import { CountServices } from './count-services';

describe('CountServices', () => {
  let service: CountServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CountServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
