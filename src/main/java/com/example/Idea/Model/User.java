package com.example.Idea.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
@Entity
@Table(name ="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_name", nullable = false, length = 50)
    private String name;

    @Column(name = "aadhar_number", nullable = false, length = 12)

    private String aadhar;

    @Column(name = "mobile_number", nullable = false)
    private String mobileNumber;

    @Column(name = "email_id")
    private String email;

 @JsonManagedReference
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,fetch = FetchType.EAGER)
    private List<Bank> bankList=new ArrayList<>();

}