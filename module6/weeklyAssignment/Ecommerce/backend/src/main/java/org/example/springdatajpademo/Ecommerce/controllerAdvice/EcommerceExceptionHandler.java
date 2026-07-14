package org.example.springdatajpademo.Ecommerce.controllerAdvice;

import org.example.springdatajpademo.Ecommerce.exceptions.CustomerNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.OrderNotFound;
import org.example.springdatajpademo.Ecommerce.exceptions.ProductNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class EcommerceExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(EcommerceExceptionHandler.class);

    @ExceptionHandler(OrderNotFound.class)
    public ResponseEntity<Map<String,String>> handler1(OrderNotFound e){
        return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
    }

    @ExceptionHandler(CustomerNotFound.class)
    public ResponseEntity<Map<String,String>> handler2(CustomerNotFound e){
        return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
    }

    @ExceptionHandler(ProductNotFound.class)
    public ResponseEntity<Map<String,String>> handler3(ProductNotFound e){
        return ResponseEntity.status(404).body(Map.of("error",e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> handler4(MethodArgumentNotValidException e){
        Map<String,String> map=new HashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(error -> map.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.status(400).body(map);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handler5(IllegalArgumentException e){
        logger.error("Invalid argument provided", e);
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String,String>> handler7(AccessDeniedException e){
        logger.warn("Access denied", e);
        return ResponseEntity.status(403).body(Map.of("error", "Access is denied"));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,String>> handler6(RuntimeException e){
        logger.error("Runtime exception occurred", e);
        return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
    }

}
