import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Componentlifecycle } from './componentlifecycle/componentlifecycle';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,Componentlifecycle],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('forms-demo');
  selectedBank = 'idfc';
  changeBank(){
    this.selectedBank = 'hdfc';
  }
}
