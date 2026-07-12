import {Component, inject} from '@angular/core';
import {CountServices} from '../../services/count-services';

@Component({
  selector: 'app-increment',
  imports: [],
  templateUrl: './increment.html',
  styleUrl: './increment.css',
})
export class Increment {
  countService:CountServices=inject(CountServices);
}
