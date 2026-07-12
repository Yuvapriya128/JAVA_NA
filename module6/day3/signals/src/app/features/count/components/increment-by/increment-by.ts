import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-increment-by',
  imports: [FormsModule],
  templateUrl: './increment-by.html',
  styleUrl: './increment-by.css',
})
export class IncrementBy {
  countService:CountServices=inject(CountServices);

  public value:number=0;
}
