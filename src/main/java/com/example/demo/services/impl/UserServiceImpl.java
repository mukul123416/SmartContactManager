package com.example.demo.services.impl;

import com.example.demo.entities.User;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.UserRepository;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public User addUser(User user) {
        return this.userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return this.userRepository.findAll();
    }

    @Override
    public User getSingleUserById(int id) {
        User user = this.userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not find with particular id !! :"+id));
        return user;
    }

    @Override
    public void deleteUserById(int id) {
        this.userRepository.deleteById(id);
    }

    @Override
    public User updateUserById(User user, int id) {
        User user1 = this.userRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("User not find with particular id !! :"+id));
        user1.setName(user.getName());
        user1.setRole(user.getRole());
        user1.setEmail(user.getEmail());
        user1.setAbout(user.getAbout());
        user1.setContacts(user.getContacts());
        user1.setPassword(user.getPassword());
        user1.setImageUrl(user.getImageUrl());
        user1.setEnabled(user.isEnabled());
        User updatedUser = this.userRepository.save(user1);
        return updatedUser;
    }
}
