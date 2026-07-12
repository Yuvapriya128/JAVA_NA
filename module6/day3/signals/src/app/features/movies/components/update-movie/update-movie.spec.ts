import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateMovie } from './update-movie';

describe('UpdateMovie', () => {
  let component: UpdateMovie;
  let fixture: ComponentFixture<UpdateMovie>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateMovie],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateMovie);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
