import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookComponents } from './book-components';

describe('BookComponents', () => {
  let component: BookComponents;
  let fixture: ComponentFixture<BookComponents>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BookComponents],
    }).compileComponents();

    fixture = TestBed.createComponent(BookComponents);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
