import {Component, inject} from '@angular/core';
import {NameServices} from '../../services/name-services';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-add-name',
  imports: [FormsModule],
  templateUrl: './add-name.html',
  styleUrls: ['./add-name.css'],
})
export class AddName {
  public newName:string='';
  nameServices:NameServices=inject(NameServices);
}
