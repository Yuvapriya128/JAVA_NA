import { TestBed } from '@angular/core/testing';

import { BookServices } from './book-services';

describe('BookServices', () => {
  let service: BookServices;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookServices);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
