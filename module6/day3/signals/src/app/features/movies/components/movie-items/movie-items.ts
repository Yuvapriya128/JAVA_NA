import {Component, inject, Input} from '@angular/core';
import {MovieServices} from '../../services/movie-services';
import MovieDTO from '../../dto/MovieDTO';

@Component({
  selector: 'app-movie-items',
  imports: [],
  templateUrl: './movie-items.html',
  styleUrl: './movie-items.css',
})
export class MovieItems {
  @Input()
  public movie!: MovieDTO;

  movieServices:MovieServices=inject(MovieServices);
}
