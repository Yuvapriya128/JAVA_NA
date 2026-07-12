import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdatePeople } from './update-people';

describe('UpdatePeople', () => {
  let component: UpdatePeople;
  let fixture: ComponentFixture<UpdatePeople>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdatePeople],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdatePeople);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
