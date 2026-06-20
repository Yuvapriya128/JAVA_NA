package com.demo.springbootdemo.controller;

import com.demo.springbootdemo.dao.MovieDao;
import com.demo.springbootdemo.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {
    @Autowired
    public MovieDao movieDao;

    @RequestMapping("")
    public Collection<Movie> findAll(){
        return  movieDao.findAll();
    }

//    There is request parameter
/*   @RequestMapping("/mquery")
     public Movie findById(@RequestParam int id){
    return movieDao.findById(id);
    single -> ?id=1
    multiple -> &name=hello&director=myself
}*/


    @RequestMapping("/{id}")
    public Movie findById(@PathVariable int id){
        return movieDao.findById(id);
    }

    @RequestMapping("/delete/{id}")
    public String deleteById(@PathVariable int id){

        movieDao.deleteById(id);
        return "Movie deleted";
    }

    @RequestMapping("/save/{id}/{title}/{director}/{year}/{genre}/{rating}")
    public void addMovie(@PathVariable int id,@PathVariable String title,@PathVariable String director,@PathVariable int year,@PathVariable String genre,@PathVariable double rating){

        movieDao.save(new Movie(id,title,director,year,genre,rating));

    }
    @RequestMapping("/update/{id}/{title}/{director}/{year}/{genre}/{rating}")
    public String  updateById(@PathVariable int id,@PathVariable String title,@PathVariable String director,@PathVariable int year,@PathVariable String genre,@PathVariable double rating){

        movieDao.updateById(id,new Movie(id,title,director,year,genre,rating));
        return ("Movie saved");
    }

}
