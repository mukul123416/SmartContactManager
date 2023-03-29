package com.example.demo.repositories;

import com.example.demo.entities.Contact;
import com.example.demo.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<Contact,Integer> {

    @Query("select u from Contact u where u.user.id = :userId")
    public Page<Contact> findContactByUser(@Param("userId")int userId, Pageable pageable);
    public List<Contact> findByNameContainingAndUser(String keyword, User user);
}
