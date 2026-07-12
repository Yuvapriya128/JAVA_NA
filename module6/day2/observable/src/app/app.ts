import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ShowCount} from './features/count/components/show-count/show-count';
import {ShowPeople} from './features/people/components/show-people/show-people';
import {BookComponents} from './features/book/components/book-components/book-components';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,ShowCount,ShowPeople,BookComponents],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('observable');
}
