import { CommonModule, JsonPipe } from '@angular/common';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-normalform',
  imports: [FormsModule,JsonPipe,CommonModule],
  templateUrl: './normalform.html',
  styleUrl: './normalform.css',
})
export class Normalform {
  newCustomer = {
    name:'',
    age:0
  }
}
