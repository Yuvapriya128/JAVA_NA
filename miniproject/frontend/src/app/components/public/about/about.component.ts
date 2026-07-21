import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './about.component.html',
  styleUrl: './about.component.css'
})
export class AboutComponent {
  readonly timeline = [
    { year: '2018', event: 'Founded with a mission to simplify digital lending workflows.' },
    { year: '2020', event: 'Launched AI-backed underwriting and portfolio intelligence.' },
    { year: '2022', event: 'Expanded to enterprise multi-role operations and compliance tooling.' },
    { year: '2025', event: 'Scaled into a unified lending operations cloud platform.' }
  ];
}
