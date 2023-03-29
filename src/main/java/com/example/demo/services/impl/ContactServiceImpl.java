package com.example.demo.services.impl;

import com.example.demo.entities.Contact;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.ContactRepository;
import com.example.demo.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private ContactRepository contactRepository;
    @Override
    public Contact addContact(Contact contact) {
        return this.contactRepository.save(contact);
    }

    @Override
    public List<Contact> getAllContacts() {
        return this.contactRepository.findAll();
    }

    @Override
    public Contact getSingleContactById(int id) {
        Contact contact = this.contactRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Contact not find with particular id !! :"+id));
        return contact;
    }

    @Override
    public void deleteContactById(int id) {
        this.contactRepository.deleteById(id);
    }

    @Override
    public Contact updateContactById(Contact contact, int id) {
        Contact contact1 = this.contactRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Contact not find with particular id !! :"+id));
        contact1.setName(contact.getName());
        contact1.setSecondName(contact.getSecondName());
        contact1.setEmail(contact.getEmail());
        contact1.setWork(contact.getWork());
        contact1.setImage(contact.getImage());
        contact1.setPhone(contact.getPhone());
        contact1.setDescription(contact.getDescription());
        Contact updatedContact = this.contactRepository.save(contact1);
        return updatedContact;
    }
}
