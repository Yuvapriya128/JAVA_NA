import { Component, EventEmitter, Input, Output, ViewChild } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-add-person',
  imports: [FormsModule, CommonModule],
  templateUrl: './add-person.html',
  styleUrl: './add-person.css'
})
export class AddPerson {

  @ViewChild('form') form!: NgForm;

  @Input()
  person: PersonDTO = {
    id: 0,
    name: '',
    age: 0,
    email: ''
  };

  isUpdate = false;

  @Output()
  onAdd = new EventEmitter<PersonDTO>();

  @Output()
  onUpdate = new EventEmitter<PersonDTO>();

  // Validation patterns
  namePattern = /^[a-zA-Z\s]*$/; // Only alphabets and spaces
  emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;

  // Validation state tracking
  isFormTouched = false;

  isFormValid(): boolean {
    if (!this.form) {
      return false;
    }
    return this.form.valid === true && this.isValidName() && this.isValidAge() && this.isValidEmail();
  }

  isValidName(): boolean {
    return this.person.name.trim().length > 0 && this.namePattern.test(this.person.name);
  }

  isValidAge(): boolean {
    return this.person.age !== null && this.person.age > 0 && this.person.age <= 120;
  }

  isValidEmail(): boolean {
    return this.emailPattern.test(this.person.email);
  }

  getNameErrorMessage(): string {
    if (!this.person.name || this.person.name.trim().length === 0) {
      return 'Name is required';
    }
    if (!this.namePattern.test(this.person.name)) {
      return 'Name must contain only alphabets and spaces';
    }
    return '';
  }

  getAgeErrorMessage(): string {
    if (this.person.age === null || this.person.age === 0) {
      return 'Age is required';
    }
    if (this.person.age < 1) {
      return 'Age must be greater than 0';
    }
    if (this.person.age > 120) {
      return 'Age must be 120 or less';
    }
    return '';
  }

  getEmailErrorMessage(): string {
    if (!this.person.email || this.person.email.trim().length === 0) {
      return 'Email is required';
    }
    if (!this.emailPattern.test(this.person.email)) {
      return 'Please enter a valid email address';
    }
    return '';
  }

  addPerson(){
    if (this.isFormValid()) {
      this.onAdd.emit({...this.person});

      this.person={
        id:0,
        name:'',
        age:0,
        email:''
      };
      this.isFormTouched = false;
      this.form.resetForm();
    }
  }

  updatePerson(){
    if (this.isFormValid()) {
      this.onUpdate.emit({...this.person});

      this.person={
        id:0,
        name:'',
        age:0,
        email:''
      };

      this.isUpdate=false;
      this.isFormTouched = false;
      this.form.resetForm();
    }
  }

}
