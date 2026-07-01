package org.example.junittests.exception;

public class EmployeeNotFound extends RuntimeException{
    public EmployeeNotFound(String msg){
        super(msg);
    }
}
