import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';

@Component({
  selector: 'app-decrement',
  imports: [],
  templateUrl: './decrement.html',
  styleUrl: './decrement.css',
})
export class Decrement {
  countService:CountServices=inject(CountServices);
}
