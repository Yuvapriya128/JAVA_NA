import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Structuraldirectives } from './structuraldirectives';

describe('Structuraldirectives', () => {
  let component: Structuraldirectives;
  let fixture: ComponentFixture<Structuraldirectives>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Structuraldirectives],
    }).compileComponents();

    fixture = TestBed.createComponent(Structuraldirectives);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
