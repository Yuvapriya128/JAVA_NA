import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Specialform } from './specialform';

describe('Specialform', () => {
  let component: Specialform;
  let fixture: ComponentFixture<Specialform>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Specialform],
    }).compileComponents();

    fixture = TestBed.createComponent(Specialform);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
