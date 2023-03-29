package com.example.demo.payloads;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ErrorResponse {

    public static ResponseEntity<Object> ResponseHandler(String message, Boolean error, HttpStatus status){
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("message",message);
        map.put("error",error);
        map.put("status",status.value());
        return new ResponseEntity<Object>(map,status);
    }
}
