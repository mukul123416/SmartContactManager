package com.example.demo.services;

import com.example.demo.entities.Parent;

import java.util.List;

public interface ParentService {

    public Parent addParent(Parent parent);
    public List<Parent> getAllParent();
    public Parent getSingleParentById(int id);
    public void deleteParentById(int id);
    public Parent updateParentById(Parent parent,int id);

}
