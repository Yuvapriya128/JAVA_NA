import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-content-wrapper',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './content-wrapper.component.html',
  styleUrl: './content-wrapper.component.css'
})
export class ContentWrapperComponent {
  @Input() padding: 'sm' | 'md' | 'lg' = 'md';
  @Input() maxWidth: 'sm' | 'md' | 'lg' | 'full' = 'lg';
  @Input() className = '';
}

