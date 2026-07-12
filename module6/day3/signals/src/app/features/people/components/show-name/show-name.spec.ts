import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowName } from './show-name';

describe('ShowName', () => {
  let component: ShowName;
  let fixture: ComponentFixture<ShowName>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowName],
    }).compileComponents();

    fixture = TestBed.createComponent(ShowName);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
