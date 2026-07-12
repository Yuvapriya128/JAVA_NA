import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Decrement } from './decrement';

describe('Decrement', () => {
  let component: Decrement;
  let fixture: ComponentFixture<Decrement>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Decrement],
    }).compileComponents();

    fixture = TestBed.createComponent(Decrement);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
