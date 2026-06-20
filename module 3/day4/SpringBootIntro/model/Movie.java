package com.demo.springbootdemo.model;

public class Movie {
    private int id;
    private String title;
    private String director;
    private int year;
    private String genre;
    private double rating;

    public Movie(){}

    public Movie(String title, String director, int year, String genre, double rating) {
        this.title = title;
        this.director = director;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
    }

    public Movie(int id, String title, String director, int year, String genre, double rating) {
        this.id = id;
        this.title = title;
        this.director = director;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
    }

    @Override
    public String toString(){
        return ("Movie[ "+id+" "+title+" "+director+" "+year+" "+genre+" "+rating+" ]");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }
}
