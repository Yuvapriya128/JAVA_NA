package org.example.springdatajpademo.ProjectEmplyoee.exceptions;

public class EmpNotFound extends RuntimeException{
    public EmpNotFound(String message) {
        super(message);
    }
}
