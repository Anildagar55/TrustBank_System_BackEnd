package com.example.Idea.Model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NonNull;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "bank")
public class Bank {
    @Id
    private String customerId;
    @Enumerated(EnumType.STRING)
    private Banktype banktype=Banktype.BOB;

    private long userid;
  @Column(unique = true)
    private String accountNumber;
  @Column(name = "Account_holder_name",nullable = false)
    private String holderName;
  @Column(name = "Mobile_Number",nullable = false)
    private String mobileNumber;
@Column(name = "Email")
  private String email;
@Column(name = "Amount",nullable = false)
    private double amount;

    public enum Banktype{
        BOB,SBI,BOI,CANARA,PNB,UCO,HDFC
    }
  @JsonBackReference
    @ManyToOne(fetch =FetchType.EAGER)
    @JoinColumn(name = "user_id")

    private User user;
}
