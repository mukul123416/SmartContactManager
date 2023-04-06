package com.example.demo.entities;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "NOTIFICATION")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int notificationId;

    private String message;

    private boolean isRead;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "CREATED_AT", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    @Column(name = "UPDATED_AT", nullable = false)
    private Date updatedAt;

    @ManyToOne
    private User user;
}
