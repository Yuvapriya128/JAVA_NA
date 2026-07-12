import {Component, inject} from '@angular/core';
import {MovieServices} from '../../services/movie-services';
import {AddMovie} from '../add-movie/add-movie';
import {UpdateMovie} from '../update-movie/update-movie';
import {MovieItems} from '../movie-items/movie-items';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-movie-components',
  imports: [AddMovie,UpdateMovie,MovieItems,FormsModule],
  templateUrl: './movie-components.html',
  styleUrls: ['./movie-components.css'],
})
export class MovieComponents {
  protected showupdate: boolean = false;

  movieServices:MovieServices=inject(MovieServices);
}
