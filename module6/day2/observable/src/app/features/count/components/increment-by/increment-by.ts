import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';

@Component({
  selector: 'app-increment-by',
  imports: [],
  templateUrl: './increment-by.html',
  styleUrl: './increment-by.css',
})
export class IncrementBy {
  countService:CountServices=inject(CountServices);
}
