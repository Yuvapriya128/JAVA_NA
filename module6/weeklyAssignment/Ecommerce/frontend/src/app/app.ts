import { Component, inject, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AppStateService } from './core/state/app-state.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  private readonly appState = inject(AppStateService);
  protected readonly title = signal('Ecom');

  constructor() {
    this.appState.theme();
  }
}
