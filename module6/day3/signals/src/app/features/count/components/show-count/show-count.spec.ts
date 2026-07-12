import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowCount } from './show-count';

describe('ShowCount', () => {
  let component: ShowCount;
  let fixture: ComponentFixture<ShowCount>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ShowCount],
    }).compileComponents();

    fixture = TestBed.createComponent(ShowCount);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
