import {Component, inject} from '@angular/core';
import {MovieServices} from '../../services/movie-services';
import {FormsModule} from '@angular/forms';
import MovieDTO from '../../dto/MovieDTO';

@Component({
  selector: 'app-add-movie',
  imports: [FormsModule],
  templateUrl: './add-movie.html',
  styleUrls: ['./add-movie.css'],
})
export class AddMovie {
  public newMovie:MovieDTO={
    id:0,
    title:'',
    director:'',
    genre:'',
    rating:0
  }
  movieServices:MovieServices=inject(MovieServices);
}
