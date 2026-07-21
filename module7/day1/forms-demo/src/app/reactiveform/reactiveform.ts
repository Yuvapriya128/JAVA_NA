import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';

@Component({
  selector: 'app-reactiveform',
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './reactiveform.html',
  styleUrl: './reactiveform.css',
})
export class Reactiveform {


  private fb:FormBuilder = inject(FormBuilder);

  customerForm = this.fb.group ({
    name:new FormControl('',Validators.required),
    age:new FormControl<number | null>(null, [Validators.required,Validators.min(18)]),
    email:new FormControl('', [Validators.required,Validators.email])

  }
  
    
  )

  handleSubmit() {
console.log(this.customerForm.value);
}
  isLogged=true;
handleLogout() {
this.isLogged=false;
}


employees = [
  'John',
  'David',
  'Mary',
  'Steve'
];
status = 'Approved';
}
