import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowPeople } from './show-people';

describe('ShowPeople', () => {
  let component: ShowPeople;
  let fixture: ComponentFixture<ShowPeople>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowPeople],
    }).compileComponents();

    fixture = TestBed.createComponent(ShowPeople);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
