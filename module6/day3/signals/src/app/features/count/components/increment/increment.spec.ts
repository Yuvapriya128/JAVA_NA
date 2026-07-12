import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Increment } from './increment';

describe('Increment', () => {
  let component: Increment;
  let fixture: ComponentFixture<Increment>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Increment],
    }).compileComponents();

    fixture = TestBed.createComponent(Increment);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
