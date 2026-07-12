import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {People} from './features/people/component/people/people';
import {PersonComponent} from './features/people/component/person-component/person-component';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,People],
  templateUrl: './app.html'
})
export class App {
  protected  title = 'pubsub';
}
