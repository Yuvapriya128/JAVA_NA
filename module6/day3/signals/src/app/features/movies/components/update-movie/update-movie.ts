import {Component, inject} from '@angular/core';
import {MovieServices} from '../../services/movie-services';
import {FormsModule} from '@angular/forms';
import MovieDTO from '../../dto/MovieDTO';

@Component({
  selector: 'app-update-movie',
  imports: [FormsModule],
  templateUrl: './update-movie.html',
  styleUrls: ['./update-movie.css'],
})
export class UpdateMovie {
  public newMovie:MovieDTO={
    id:0,
    title:'',
    director:'',
    genre:'',
    rating:0
  }
  movieServices:MovieServices=inject(MovieServices);
}
