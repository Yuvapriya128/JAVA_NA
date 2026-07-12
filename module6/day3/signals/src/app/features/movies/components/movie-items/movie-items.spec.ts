import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieItems } from './movie-items';

describe('MovieItems', () => {
  let component: MovieItems;
  let fixture: ComponentFixture<MovieItems>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovieItems],
    }).compileComponents();

    fixture = TestBed.createComponent(MovieItems);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
