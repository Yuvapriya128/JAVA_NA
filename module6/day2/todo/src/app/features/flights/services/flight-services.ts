import { Injectable } from '@angular/core';
import FlightDTO from '../../dto/FlightDTO';

@Injectable({
  providedIn: 'root'
})
export class FlightServices {

  private flights: FlightDTO[] = [
    {
      id: 1,
      name: 'Air India',
      source: 'Chennai',
      destination: 'Delhi',
      completed: true
    },
    {
      id: 2,
      name: 'IndiGo',
      source: 'Bengaluru',
      destination: 'Mumbai',
      completed: false
    },
    {
      id: 3,
      name: 'SpiceJet',
      source: 'Hyderabad',
      destination: 'Kolkata',
      completed: false
    },
    {
      id: 4,
      name: 'Vistara',
      source: 'Delhi',
      destination: 'Goa',
      completed: true
    }
  ];

  public getFlights(): FlightDTO[] {
    return this.flights;
  }

  public addFlight(flight: FlightDTO): FlightDTO {
    this.flights.push(flight);
    return flight;
  }

  public updateFlight(updatedFlight: FlightDTO): void {
    const index = this.flights.findIndex(
      flight => flight.id === updatedFlight.id
    );

    if (index !== -1) {
      this.flights[index] = updatedFlight;
    }
  }

  public deleteFlight(id: number): void {
    this.flights = this.flights.filter(
      flight => flight.id !== id
    );
  }
}
