import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';
import {Increment} from '../increment/increment';
import {IncrementBy} from '../increment-by/increment-by';
import {Decrement} from '../decrement/decrement';
import {DecrementBy} from '../decrement-by/decrement-by';

@Component({
  selector: 'app-show-count',
  imports: [Increment,IncrementBy,Decrement,DecrementBy],
  templateUrl: './show-count.html',
  styleUrl: './show-count.css',
})
export class ShowCount {
  countService:CountServices=inject(CountServices);
}
