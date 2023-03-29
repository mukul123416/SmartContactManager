package com.example.demo.services;

import com.example.demo.entities.Contact;
import com.example.demo.entities.Parent;
import com.example.demo.entities.User;

import java.util.List;

public interface ContactService {

    public Contact addContact(Contact contact);
    public List<Contact> getAllContacts();
    public Contact getSingleContactById(int id);
    public void deleteContactById(int id);
    public Contact updateContactById(Contact contact,int id);
}
