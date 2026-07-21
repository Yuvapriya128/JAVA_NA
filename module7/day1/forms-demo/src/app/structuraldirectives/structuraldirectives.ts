import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-structuraldirectives',
  imports: [CommonModule],
  templateUrl: './structuraldirectives.html',
  styleUrl: './structuraldirectives.css',
})
export class Structuraldirectives {
  isLogged=true;

  employees = [
    'Gopi Kant',
    'Akash Bharat',
    'Renjitha',
    'Vishnu'
  ]

  status='';
}
