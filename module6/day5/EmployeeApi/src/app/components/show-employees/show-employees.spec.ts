import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowEmployees } from './show-employees';

describe('ShowEmployees', () => {
  let component: ShowEmployees;
  let fixture: ComponentFixture<ShowEmployees>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowEmployees],
    }).compileComponents();

    fixture = TestBed.createComponent(ShowEmployees);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
