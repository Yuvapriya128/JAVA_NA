import {NgClass} from '@angular/common';
import {Component, inject, Input} from '@angular/core';
import FlightDTO from '../../../dto/FlightDTO';
import {FlightServices} from '../../services/flight-services';

@Component({
  selector: 'app-flight-item',
  imports: [NgClass],
  templateUrl: './flight-item.html',
  styleUrl: './flight-item.css',
})
export class FlightItem {
  @Input()
  flight!: FlightDTO;

  public flightService = inject(FlightServices);
}
