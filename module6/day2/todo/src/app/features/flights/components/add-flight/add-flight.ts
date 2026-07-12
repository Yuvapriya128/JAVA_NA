import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import FlightDTO from '../../../dto/FlightDTO';
import {FlightServices} from '../../services/flight-services';

@Component({
  selector: 'app-add-flight',
  imports: [FormsModule],
  templateUrl: './add-flight.html',
  styleUrl: './add-flight.css',
})
export class AddFlight {
  public flightService: FlightServices = inject(FlightServices);

  public newFlight: FlightDTO = {
    id: 0,
    name: '',
    source: '',
    destination: '',
    completed: false
  };

  addFlight(newFlight: FlightDTO) {
    if (
      newFlight.id > 0 &&
      newFlight.name.trim() !== '' &&
      newFlight.source.trim() !== '' &&
      newFlight.destination.trim() !== ''
    ) {
      this.flightService.addFlight(newFlight);
    }

    this.newFlight = {
      id: 0,
      name: '',
      source: '',
      destination: '',
      completed: false
    };
  }
}
