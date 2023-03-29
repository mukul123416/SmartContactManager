package com.example.demo.services.impl;

import com.example.demo.entities.Parent;
import com.example.demo.entities.Student;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.ParentRepository;
import com.example.demo.services.ParentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ParentServiceImpl implements ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Override
    public Parent addParent(Parent parent) {

        List<Student> studentList = new ArrayList<>();

        //create first student
        Student student = new Student();
        student.setFullName("Rajesh Sharma");
        student.setAge(10);

        studentList.add(student);

        student.setParent(parent);
        parent.setStudentSet(studentList);

        return this.parentRepository.save(parent);
    }

    @Override
    public List<Parent> getAllParent() {
        return this.parentRepository.findAll();
    }

    @Override
    public Parent getSingleParentById(int id) {
        return this.parentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Parent not find with particular id !! :"+id));
    }

    @Override
    public void deleteParentById(int id) {
        this.parentRepository.deleteById(id);
    }

    @Override
    public Parent updateParentById(Parent parent, int id) {
        Parent parent1 = this.parentRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Parent not find with particular id !! :"+id));
        parent1.setFullName(parent.getFullName());
        parent1.setAge(parent.getAge());
        parent1.setStudentSet(parent.getStudentSet());
        return this.parentRepository.save(parent1);
    }
}
