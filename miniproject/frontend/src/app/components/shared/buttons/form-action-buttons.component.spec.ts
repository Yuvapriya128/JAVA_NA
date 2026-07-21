import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FormActionButtonsComponent } from './form-action-buttons.component';

describe('FormActionButtonsComponent', () => {
  let component: FormActionButtonsComponent;
  let fixture: ComponentFixture<FormActionButtonsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FormActionButtonsComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(FormActionButtonsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
