package com.example.demo.repositories;

import com.example.demo.entities.Notification;
import com.example.demo.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification,Integer> {

    List<User> findAllByUser(User user);

    List<Notification> findAllByIsRead(boolean misread);

}
