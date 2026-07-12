import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';
import {Increment} from '../increment/increment';
import {IncrementBy} from '../increment-by/increment-by';
import {Decrement} from '../decrement/decrement';
import {DecrementBy} from '../decrement-by/decrement-by';
import {AsyncPipe} from '@angular/common';

@Component({
  selector: 'app-show-count',
  imports: [Increment,IncrementBy,Decrement,DecrementBy,AsyncPipe],
  templateUrl: './show-count.html',
  styleUrls: ['./show-count.css'],
})
export class ShowCount {
  countService:CountServices=inject(CountServices);
}
