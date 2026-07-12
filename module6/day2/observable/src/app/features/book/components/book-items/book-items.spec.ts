import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookItems } from './book-items';

describe('BookItems', () => {
  let component: BookItems;
  let fixture: ComponentFixture<BookItems>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookItems],
    }).compileComponents();

    fixture = TestBed.createComponent(BookItems);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
