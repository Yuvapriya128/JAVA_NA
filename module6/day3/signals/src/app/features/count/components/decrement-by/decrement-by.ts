import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-decrement-by',
  imports: [FormsModule],
  templateUrl: './decrement-by.html',
  styleUrl: './decrement-by.css',
})
export class DecrementBy {
  countService:CountServices=inject(CountServices);
  public value:number=0;
}
