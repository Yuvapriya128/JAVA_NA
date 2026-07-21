import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Attributedirectives } from './attributedirectives';

describe('Attributedirectives', () => {
  let component: Attributedirectives;
  let fixture: ComponentFixture<Attributedirectives>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Attributedirectives],
    }).compileComponents();

    fixture = TestBed.createComponent(Attributedirectives);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
