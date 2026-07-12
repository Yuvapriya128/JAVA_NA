import { TestBed } from '@angular/core/testing';

import { PeopleServices } from './people-services';

describe('PeopleServices', () => {
  let service: PeopleServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PeopleServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
