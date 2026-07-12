import {Component, inject} from '@angular/core';
import {FlightItem} from '../flight-item/flight-item';
import {FlightServices} from '../../services/flight-services';
import {FormsModule} from '@angular/forms';
import {AddFlight} from '../add-flight/add-flight';
import {UpdateFlight} from '../update-flight/update-flight';

@Component({
  selector: 'app-flights-component',
  imports: [FormsModule, FlightItem, AddFlight, UpdateFlight],
  templateUrl: './flights-component.html',
  styleUrl: './flights-component.css',
})
export class FlightsComponent {
  public flightService = inject(FlightServices);
  showUpdate = false;
}
