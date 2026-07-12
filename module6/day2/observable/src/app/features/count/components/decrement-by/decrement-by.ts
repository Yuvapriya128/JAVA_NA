import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';

@Component({
  selector: 'app-decrement-by',
  imports: [],
  templateUrl: './decrement-by.html',
  styleUrl: './decrement-by.css',
})
export class DecrementBy {
  countService:CountServices=inject(CountServices);
}
