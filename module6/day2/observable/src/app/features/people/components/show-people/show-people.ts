import {Component, inject} from '@angular/core';
import {PeopleServices} from '../../services/people-services';
import {AsyncPipe} from '@angular/common';
import {AddPeople} from '../add-people/add-people';
import {UpdatePeople} from '../update-people/update-people';

@Component({
  selector: 'app-show-people',
  imports: [AsyncPipe,AddPeople,UpdatePeople],
  templateUrl: './show-people.html',
  styleUrl: './show-people.css',
})
export class ShowPeople {
  peopleServices:PeopleServices=inject(PeopleServices);
}
