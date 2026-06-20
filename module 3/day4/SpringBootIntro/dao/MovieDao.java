package com.demo.springbootdemo.dao;

import com.demo.springbootdemo.model.Movie;

import java.util.Collection;

public interface MovieDao {
    void save(Movie movie);
    void deleteById(int id);
    Movie findById(int id);
    Collection<Movie> findAll();
    void updateById(int id,Movie movie);
}
