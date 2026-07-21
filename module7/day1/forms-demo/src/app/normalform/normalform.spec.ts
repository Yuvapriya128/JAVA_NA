import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Normalform } from './normalform';

describe('Normalform', () => {
  let component: Normalform;
  let fixture: ComponentFixture<Normalform>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Normalform],
    }).compileComponents();

    fixture = TestBed.createComponent(Normalform);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
