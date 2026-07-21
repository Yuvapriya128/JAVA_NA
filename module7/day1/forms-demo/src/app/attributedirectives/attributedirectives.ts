import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-attributedirectives',
  imports: [CommonModule],
  templateUrl: './attributedirectives.html',
  styleUrl: './attributedirectives.css',
})
export class Attributedirectives {
  marks=86;
  textColor='red';
  fontsize='small';
  isMore=true;
}
