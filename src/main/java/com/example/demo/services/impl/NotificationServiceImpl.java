package com.example.demo.services.impl;

import com.example.demo.entities.Notification;
import com.example.demo.exceptions.ResourceNotFoundException;
import com.example.demo.repositories.NotificationRepository;
import com.example.demo.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public Notification addNotification(Notification notification) {
        return this.notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getAllNotifications() {
        return this.notificationRepository.findAll();
    }

    @Override
    public Notification getSingleNotificationById(int id) {
        return this.notificationRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Notification not find with particular id !! :"+id));
    }

    @Override
    public void deleteNotificationById(int id) {
        this.notificationRepository.deleteById(id);
    }

    @Override
    public Notification updateNotificationById(Notification user, int id) {
        Notification notification = this.notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Notification not find with particular id !! :" + id));
        notification.setMessage(user.getMessage());
        notification.setUser(user.getUser());
        notification.setRead(user.isRead());
        Notification updatedNotification = this.notificationRepository.save(notification);
        return updatedNotification;
    }
}
