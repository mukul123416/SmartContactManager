package com.example.demo.controllers;

import com.example.demo.entities.Contact;
import com.example.demo.entities.MyOrder;
import com.example.demo.entities.User;
import com.example.demo.repositories.ContactRepository;
import com.example.demo.repositories.MyOrderRepository;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@CrossOrigin
public class SearchController {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MyOrderRepository myOrderRepository;

    @GetMapping("/search/{query}")
    public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal, Model model){

        String name=principal.getName();
        User user=this.userRepository.getUserByUserName(name);

        List<Contact> contacts = this.contactRepository.findByNameContainingAndUser(query, user);

        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/admin/search/{query}")
    public ResponseEntity<?> adminSearch(@PathVariable("query") String query){

        List<User> byNameContainingAndUser = this.userRepository.findByNameContainingAndRole(query,"ROLE_USER");
        return ResponseEntity.ok(byNameContainingAndUser);
    }

    @GetMapping("/transaction/search/{query}")
    public ResponseEntity<?> transactionSearch(@PathVariable("query") String query){

        List<MyOrder> byNameContaining = this.myOrderRepository.findByOrderIdContaining(query);
        return ResponseEntity.ok(byNameContaining);
    }
}
