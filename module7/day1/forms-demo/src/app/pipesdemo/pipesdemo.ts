import { CurrencyPipe, DatePipe, DecimalPipe, JsonPipe, LowerCasePipe, SlicePipe, TitleCasePipe, UpperCasePipe } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-pipesdemo',
  imports: [UpperCasePipe,LowerCasePipe,TitleCasePipe,DatePipe,CurrencyPipe,DecimalPipe,JsonPipe,SlicePipe],
  templateUrl: './pipesdemo.html',
  styleUrl: './pipesdemo.css',
})
export class Pipesdemo {
  title="PipesDemo using component";
  today = new Date();
  salary  = 78452.9808989;

  newCustomer = {
    name:'Ram',
    email:'ram@gmail.com',
    age:22
  }
}
