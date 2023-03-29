package com.example.demo.entities;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

@Entity
@Table(name = "CONTACT")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int cId;

    @NotBlank(message="User name can not be empty !!")
    @Size(min=3,max=12,message="User name must be between 3 - 12 characters !!")
    private String name;

    @NotBlank(message="Nick name can not be empty !!")
    @Size(min=3,max=12,message="User name must be between 3 - 12 characters !!")
    private String secondName;

    @NotBlank(message="Work name can not be empty !!")
    @Size(min=3,max=50,message="Work name must be between 3 - 50 characters !!")
    private String work;

    @NotBlank(message="Email can not be empty !!")
    @Email(regexp="^[a-zA-Z0-9+_.-]+@[a-zA-Z0-9.-]+$")
    private String email;

    @NotBlank(message="Phone number can not be empty !!")
    private String phone;

    private String image;

    @NotBlank(message="Description can not be empty !!")
    @Size(min=10,max=1000,message="Description must be between 10 - 1000 characters !!")
    @Column(length = 1000)
    private String description;

    @ManyToOne
    @JsonIgnore
    private User user;

}
