import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Pipesdemo } from './pipesdemo';

describe('Pipesdemo', () => {
  let component: Pipesdemo;
  let fixture: ComponentFixture<Pipesdemo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Pipesdemo],
    }).compileComponents();

    fixture = TestBed.createComponent(Pipesdemo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
