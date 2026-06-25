package org.example.springdatajpademo.Ecommerce.controllerAdvice;

import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.OrderNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.ProductNotFound;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class EcommerceExceptionHandler {

    @ExceptionHandler(OrderNotFound.class)
    public ResponseEntity<Map<String,String>> handler1(OrderNotFound e){
        return ResponseEntity.status(404).body(Map.of("Message : ",e.getMessage()));
    }
    @ExceptionHandler(CustomerNotFound.class)
    public ResponseEntity<Map<String,String>> handler2(CustomerNotFound e){
        return ResponseEntity.status(404).body(Map.of("Message : ",e.getMessage()));
    }
    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<Map<String,String>> handler3(ProductNotFound e){
        return ResponseEntity.status(404).body(Map.of("Message : ",e.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handler4(MethodArgumentNotValidException e){
        Map<String,String> map=new HashMap<>();
                e.getBindingResult().getFieldErrors()
                .forEach(error -> map.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(403).body(map);
    }

}
