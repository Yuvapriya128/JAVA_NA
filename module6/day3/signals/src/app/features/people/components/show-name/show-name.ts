import {Component, inject} from '@angular/core';
import {NameServices} from '../../services/name-services';
import {AddName} from '../add-name/add-name';
import {UpdateName} from '../update-name/update-name';

@Component({
  selector: 'app-show-name',
  imports: [AddName,UpdateName],
  templateUrl: './show-name.html',
  styleUrls: ['./show-name.css'],
})
export class ShowName {
  nameServices:NameServices=inject(NameServices);
}
