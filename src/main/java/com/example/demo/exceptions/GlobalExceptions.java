package com.example.demo.exceptions;

import com.example.demo.payloads.ErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptions {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> ResourceNotFoundHandler(ResourceNotFoundException ex){
        return ErrorResponse.ResponseHandler(ex.getMessage(),true, HttpStatus.BAD_REQUEST);
    }
}
