package com.example.demo.services;

import com.example.demo.entities.User;

import java.util.List;

public interface UserService {

    public User addUser(User user);
    public List<User> getAllUser();
    public User getSingleUserById(int id);
    public void deleteUserById(int id);
    public User updateUserById(User user,int id);
}
