import {Component, inject} from '@angular/core';
import {PeopleServices} from '../../services/people-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-update-people',
  imports: [FormsModule],
  templateUrl: './update-people.html',
  styleUrl: './update-people.css',
})
export class UpdatePeople {
  peopleServices:PeopleServices=inject(PeopleServices);
  protected oldName: string='';
  protected newName: string='';
}
