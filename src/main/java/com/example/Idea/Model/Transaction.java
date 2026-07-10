package com.example.Idea.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false,unique = true,length = 30)
    private String transactionId;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER_DEBIT, TRANSFER_CREDIT,
        INTEREST_CREDIT, LOAN_DISBURSEMENT, LOAN_REPAYMENT, CHARGE
    }
    @Column(nullable = false)
    private double amount;

    private double balanceAfter;
private String description;
    @Column(length = 20)
    private String toAccountNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "bank_id", nullable = false)
    private Bank bank;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 15)
    @Builder.Default
    private TransactionStatus status=TransactionStatus.SUCCESS;

    public enum TransactionStatus {
        SUCCESS,
        FAILED,
        PENDING,
        REVERSED
    }
@CreatedDate
@Column(updatable = false)
 private LocalDateTime createdAt;
}
