import {Component, inject} from '@angular/core';
import {FormsModule} from '@angular/forms';
import FlightDTO from '../../../dto/FlightDTO';
import {FlightServices} from '../../services/flight-services';

@Component({
  selector: 'app-update-flight',
  imports: [FormsModule],
  templateUrl: './update-flight.html',
  styleUrl: './update-flight.css',
})
export class UpdateFlight {
  public flightService: FlightServices = inject(FlightServices);

  public newFlight: FlightDTO = {
    id: 0,
    name: '',
    source: '',
    destination: '',
    completed: false
  };

  public updateFlight(selectedFlight: FlightDTO): void {
    if (
      selectedFlight.id > 0 &&
      selectedFlight.name.trim() !== '' &&
      selectedFlight.source.trim() !== '' &&
      selectedFlight.destination.trim() !== ''
    ) {
      this.flightService.updateFlight(selectedFlight);

      this.newFlight = {
        id: 0,
        name: '',
        source: '',
        destination: '',
        completed: false
      };
    }
  }
}
