import {Component, inject} from '@angular/core';
import {NameServices} from '../../services/name-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-update-name',
  imports: [FormsModule],
  templateUrl: './update-name.html',
  styleUrls: ['./update-name.css'],
})
export class UpdateName {
  public oldName:string='';
  public newName:string='';
  nameServices:NameServices=inject(NameServices);
}
