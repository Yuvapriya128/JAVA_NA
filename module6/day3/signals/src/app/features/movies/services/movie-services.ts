import {Injectable, Service, signal, WritableSignal} from '@angular/core';
import MovieDTO from '../dto/MovieDTO';
import {Writable} from 'node:stream';

@Injectable({
  providedIn: 'root'
})
export class MovieServices {
  public movies: MovieDTO[] = [
    {
      id: 1,
      title: 'Interstellar',
      director: 'Christopher Nolan',
      genre: 'Sci-Fi',
      rating: 5,
    },
    {
      id: 2,
      title: 'Inception',
      director: 'Christopher Nolan',
      genre: 'Sci-Fi',
      rating: 5,
    },
    {
      id: 3,
      title: 'The Dark Knight',
      director: 'Christopher Nolan',
      genre: 'Action',
      rating: 5,
    },
    {
      id: 4,
      title: 'Avatar',
      director: 'James Cameron',
      genre: 'Fantasy',
      rating: 4,
    },
    {
      id: 5,
      title: 'Titanic',
      director: 'James Cameron',
      genre: 'Romance',
      rating: 5,
    },
    {
      id: 6,
      title: 'The Matrix',
      director: 'Lana Wachowski, Lilly Wachowski',
      genre: 'Sci-Fi',
      rating: 5,
    },
    {
      id: 7,
      title: 'Avengers: Endgame',
      director: 'Anthony Russo, Joe Russo',
      genre: 'Superhero',
      rating: 4,
    },
    {
      id: 8,
      title: 'Joker',
      director: 'Todd Phillips',
      genre: 'Drama',
      rating: 4,
    },
    {
      id: 9,
      title: 'Parasite',
      director: 'Bong Joon-ho',
      genre: 'Thriller',
      rating: 5,
    },
    {
      id: 10,
      title: 'Dune',
      director: 'Denis Villeneuve',
      genre: 'Sci-Fi',
      rating: 5,
    }
  ];
  public movies$:WritableSignal<MovieDTO[]>=signal(this.movies);

  getMovies(){
    return this.movies$;
  }
  addMovies(movie: MovieDTO){
    this.movies$.update(movies => [...movies, movie]);
  }
  updateMovies(movie: MovieDTO){
    this.movies = this.movies.map(m=>m.id===movie.id?movie:m);
    this.movies$.set(this.movies);
  }
  deleteMovie(movie: MovieDTO){
    this.movies=this.movies.filter(m=>m.id!==movie.id);
    this.movies$.set(this.movies);
  }

}
