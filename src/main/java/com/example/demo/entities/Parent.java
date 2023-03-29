package com.example.demo.entities;

import lombok.*;
import java.util.List;
import javax.persistence.*;
@Entity
@Table(name = "Parent")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int parent_id;

    public String FullName;

    public int age;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "parent")
    public List<Student> studentSet;

}
