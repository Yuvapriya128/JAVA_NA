import {Component, inject} from '@angular/core';
import {PeopleServices} from '../../services/people-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-add-people',
  imports: [FormsModule],
  templateUrl: './add-people.html',
  styleUrl: './add-people.css',
})
export class AddPeople {
  peopleServices:PeopleServices=inject(PeopleServices);
  protected newName:string='';
}
