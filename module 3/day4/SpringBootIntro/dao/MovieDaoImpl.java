package com.demo.springbootdemo.dao;

import com.demo.springbootdemo.model.Movie;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component
public class MovieDaoImpl implements MovieDao{

    Collection<Movie> movies=new LinkedList<>();

    @PostConstruct
    public void init(){
        movies.add(new Movie(1,"The hell","Hellen",2005,"horror",4.5));
        movies.add(new Movie(2,"The hell 2","Hellen",2007,"horror",4.8));
    }

    @PreDestroy
    public void destroy(){
        movies.clear();
    }
    @Override
    public void save(Movie movie) {
        movies.add(movie);
    }

    @Override
    public void deleteById(int id) {
        Movie temp=null;
        for(Movie m:movies){
            if(m.getId()==id){
                temp=m;
            }
        }
        movies.remove(temp);

    }

    @Override
    public Movie findById(int id) {

        for(Movie m:movies){
            if(m.getId()==id){
                return m;
            }
        }
      return  null;
    }

    @Override
    public Collection<Movie> findAll() {
        return movies;
    }

    @Override
    public void updateById(int id, Movie movie) {
        for(Movie m:movies){
            if(m.getId()==id){
                m.setDirector(movie.getDirector());
                m.setGenre(movie.getGenre());
                m.setRating(movie.getRating());
                m.setYear(movie.getYear());
                m.setTitle(movie.getTitle());
            }
        }
    }
}
