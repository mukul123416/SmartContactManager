package com.example.demo.services;

import com.example.demo.entities.Notification;

import java.util.List;

public interface NotificationService {

    public Notification addNotification(Notification notification);
    public List<Notification> getAllNotifications();
    public Notification getSingleNotificationById(int id);
    public void deleteNotificationById(int id);
    public Notification updateNotificationById(Notification user,int id);

}
