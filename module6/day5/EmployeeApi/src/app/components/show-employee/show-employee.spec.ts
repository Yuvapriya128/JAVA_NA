import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { provideHttpClientTesting } from '@angular/common/http/testing';

import { ShowEmployee } from './show-employee';

describe('ShowEmployee', () => {
  let component: ShowEmployee;
  let fixture: ComponentFixture<ShowEmployee>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowEmployee],
      providers: [provideHttpClient(), provideHttpClientTesting()]
    }).compileComponents();

    fixture = TestBed.createComponent(ShowEmployee);
    component = fixture.componentInstance;
    component.employee = { id: 1, name: 'Test Employee', salary: 1000 };
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
