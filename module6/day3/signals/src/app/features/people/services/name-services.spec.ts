import { TestBed } from '@angular/core/testing';

import { NameServices } from './name-services';

describe('NameServices', () => {
  let service: NameServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NameServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
