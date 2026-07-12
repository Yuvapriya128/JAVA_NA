import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateName } from './update-name';

describe('UpdateName', () => {
  let component: UpdateName;
  let fixture: ComponentFixture<UpdateName>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateName],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateName);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
