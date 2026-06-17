package bookSIOC.entity;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;

import java.util.Objects;

public class Book {
    private int id;
    private String title;
    private String author;
    private double price;

    public Book(int id, String title, String author, double price) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
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

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    @Override
    public String toString(){
        return (this.id+" "+this.title+" "+this.author+" "+this.price );
    }

//    Since Set is used , do equals and hashcode

    @Override
    public boolean equals(Object o1){
        Book b=(Book)o1;
        return this.getId()==b.getId();
    }
    @Override
    public int hashCode(){
        return Objects.hashCode(getId());
    }




}
