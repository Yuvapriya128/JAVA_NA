import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {ShowCount} from './features/count/components/show-count/show-count';
import {ShowName} from './features/people/components/show-name/show-name';
import {MovieComponents} from './features/movies/components/movie-components/movie-components';

@Component({
  selector: 'app-root',
  imports: [ShowCount,ShowName,MovieComponents],
  templateUrl: './app.html',
  styleUrls: ['./app.css']
})
export class App {
  protected  title = signal('signals');
}
