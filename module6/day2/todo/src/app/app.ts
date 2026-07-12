import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {TodosComponent} from './features/todos/components/todos-component/todos-component';
import {FlightsComponent} from './features/flights/components/flights-component/flights-component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,TodosComponent],
  //imports: [RouterOutlet,FlightsComponent],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('todo');
}
