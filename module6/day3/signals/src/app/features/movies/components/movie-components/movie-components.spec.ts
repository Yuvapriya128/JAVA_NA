import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieComponents } from './movie-components';

describe('MovieComponents', () => {
  let component: MovieComponents;
  let fixture: ComponentFixture<MovieComponents>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovieComponents],
    }).compileComponents();

    fixture = TestBed.createComponent(MovieComponents);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
