import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateTodo } from './update-todo';

describe('UpdateTodo', () => {
  let component: UpdateTodo;
  let fixture: ComponentFixture<UpdateTodo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateTodo],
    }).compileComponents();

    fixture = TestBed.createComponent(UpdateTodo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
