package com.example.demo.controllers;

import com.example.demo.entities.Parent;
import com.example.demo.payloads.ErrorResponse;
import com.example.demo.payloads.SuccessResponse;
import com.example.demo.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ParentController {
    @Autowired
    private ParentService parentService;

    @Autowired
    private Environment env;

    @Value("${spring.datasource.username}")
    private String username;

    @PostMapping("/saveParent")
    public ResponseEntity<?> CreateParent(@RequestBody Parent parent) {
        try {
            Parent parent1 = this.parentService.addParent(parent);
            return SuccessResponse.ResponseHandler("SuccessFully Created", false, HttpStatus.OK, parent1);
        } catch (Exception ex) {
            return ErrorResponse.ResponseHandler(ex.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> FindParentById(@PathVariable("id") int id) {
        Parent singleParentById = this.parentService.getSingleParentById(id);
        return SuccessResponse.ResponseHandler("SuccessFully Fetched", false, HttpStatus.OK, singleParentById);
    }

    @GetMapping("/")
    public ResponseEntity<?> FindAllParent() {
        try {
            List<Parent> allParent = this.parentService.getAllParent();
            return SuccessResponse.ResponseHandler("SuccessFully Fetched", false, HttpStatus.OK, allParent);
        } catch (Exception ex) {
            return ErrorResponse.ResponseHandler(ex.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> DeleteParentById(@PathVariable("id") int id) {
        this.parentService.deleteParentById(id);
        return SuccessResponse.ResponseHandler("SuccessFully Deleted", false, HttpStatus.OK, null);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> UpdateParentById(@RequestBody Parent parent, @PathVariable("id") int id) {
        Parent parent1 = this.parentService.updateParentById(parent, id);
        return SuccessResponse.ResponseHandler("SuccessFully Updated", false, HttpStatus.OK, parent1);
    }


    @GetMapping("/property")
    public ResponseEntity<Object> getPropertyValue(@RequestParam("key") String key) {
        try {
            Map<String,String> map = new HashMap<String,String>();
            String Password = env.getProperty(key);
            String Username=username;
            map.put("UserName",Username);
            map.put("Password",Password);
            return SuccessResponse.ResponseHandler("SuccessFully Fetched", false, HttpStatus.OK, map);
        }catch (Exception ex){
            return ErrorResponse.ResponseHandler(ex.getMessage(), true, HttpStatus.BAD_REQUEST);
        }
    }

}
