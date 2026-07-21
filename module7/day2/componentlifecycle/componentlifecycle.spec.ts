import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Componentlifecycle } from './componentlifecycle';

describe('Componentlifecycle', () => {
  let component: Componentlifecycle;
  let fixture: ComponentFixture<Componentlifecycle>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Componentlifecycle],
    }).compileComponents();

    fixture = TestBed.createComponent(Componentlifecycle);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
