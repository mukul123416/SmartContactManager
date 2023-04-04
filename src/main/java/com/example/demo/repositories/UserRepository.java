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
public interface UserRepository extends JpaRepository<User,Integer> {

    @Query("select u from User u where u.email = :email")
    public User getUserByUserName(@Param("email") String email);

    Page<User> findAllByRole(Pageable pageable,String role);

    public List<User> findByNameContainingAndRole(String keyword,String role);
}
