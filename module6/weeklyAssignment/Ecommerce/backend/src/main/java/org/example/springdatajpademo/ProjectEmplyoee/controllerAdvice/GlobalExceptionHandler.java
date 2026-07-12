package org.example.springdatajpademo.ProjectEmplyoee.controllerAdvice;

import org.example.springdatajpademo.ProjectEmplyoee.exceptions.EmpNotFound;
import org.example.springdatajpademo.ProjectEmplyoee.exceptions.ErrorMessage;
import org.example.springdatajpademo.ProjectEmplyoee.exceptions.ProjectNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EmpNotFound.class)
    public ResponseEntity<Map<String,String>> handler1(EmpNotFound e){
        Map<String,String > map=new LinkedHashMap<>();
        map.put("Message:",e.getMessage());
        return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProjectNotFound.class)
    public ResponseEntity<ErrorMessage> handler2(ProjectNotFound e){
        ErrorMessage projectNotFound=new ErrorMessage(e.getMessage());
        return  ResponseEntity.status(404).body(projectNotFound);
    }


}

