package com.example.demo.entities;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "USER")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @NotBlank(message="User Name can not be empty !!")
    @Size(min=3,max=12,message="User name must be between 3 - 12 characters !!")
    private String name;

    @NotBlank(message="Email can not be empty !!")
    @Email(regexp="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
    @Column(unique=true)
    private String email;

    @NotBlank(message="Password can not be empty !!")
    private String password;

    private String role;

    private boolean enabled;

    private String imageUrl;

    @NotBlank(message="About can not be empty !!")
    @Size(min=10,max=500,message="about must be between 10 - 500 characters !!")
    @Column(length = 500)
    private String about;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "user")
    private List<Contact> contacts = new ArrayList<>();

}
