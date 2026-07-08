package com.example.Idea.Model;

import jakarta.persistence.*;
import jakarta.persistence.criteria.Fetch;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "loans")
@EntityListeners(AuditingEntityListener.class)
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "loan_number", nullable = false, unique = true)
    private String loanNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LoanType loanType;

    @Column(nullable = false)
    private String accNumber;

    @Column(nullable = false,precision = 15,scale = 2)
    private BigDecimal principalAmount;

    @Column(nullable = false,precision = 15,scale = 2)
    private BigDecimal outstandingAmount;

    @Column(nullable = false,precision = 5,scale = 2)
    private BigDecimal interestRate;

    @Column(nullable = false)
    private Integer tenureMonths;

    @Column(nullable = false,precision = 12,scale = 2)
    private BigDecimal emiAmount;

    private LocalDateTime nextEmiDate;
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private LoanStatus status = LoanStatus.PENDING;

    @Column(length = 500)
    private String rejectionReason;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Bank bank;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public enum LoanType {
        HOME, PERSONAL, VEHICLE, EDUCATION, BUSINESS
    }

    public enum LoanStatus {
        PENDING, APPROVED, REJECTED, ACTIVE, CLOSED, DEFAULTED
    }
}
