package com.example.demo.controllers;

import com.example.demo.entities.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/index")
    public String dashboard(Model model, Principal principal){
        String email = principal.getName();
        User userByEmail = userRepository.getUserByUserName(email);
        model.addAttribute("user", userByEmail);
        model.addAttribute("title","Admin Dashboard");
        return "admin/admin_dashboard";
    }

}
