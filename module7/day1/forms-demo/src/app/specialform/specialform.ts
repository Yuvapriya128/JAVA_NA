import { CommonModule, JsonPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';

@Component({
  selector: 'app-specialform',
  imports: [FormsModule,JsonPipe,CommonModule],
  templateUrl: './specialform.html',
  styleUrl: './specialform.css',
})
export class Specialform {


  newCustomer = {
    name:'',
    age:0,
  
  }
//check specialForm.valid in (ngSubmit) in html
//   handleSubmit(){ 
//     console.log('this.newCustomer', this.newCustomer);  
//     console.log('Form Submitted', this.newCustomer);
// }
//check all the form data is valid in ts file
 handleSubmit(specialForm:NgForm){ 
    console.log('this.newCustomer', this.newCustomer);  
    if(!specialForm.valid){
      return;
    }
    console.log('Form Submitted', this.newCustomer);
}
}
