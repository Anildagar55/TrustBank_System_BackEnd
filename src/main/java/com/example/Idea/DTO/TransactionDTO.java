package com.example.Idea.DTO;

import com.example.Idea.Model.Transaction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.antlr.v4.runtime.misc.NotNull;

import java.time.LocalDateTime;

public class TransactionDTO {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DepositRequest{
        private String accountNumber;
        private double amount;
        private String description;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WithdrawRequest{

        private String accountNumber;
        private double amount;
        private String description;
    }

    // ─────────────────────────────────────────────────────────────
    //  3. TRANSFER REQUEST
    // ──────
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransferRequest{

        private String fromAccountNumber;
        private String toAccountNumber;
        private double amount;
        private String description;
    }

    // ─────────────────────────────────────────────────────────────
    //  4. TRANSACTION RESPONSE  (client ko ye return hoga)
    // ─────────────────────────────────────────────────────────────
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionResponse{
        private Long id;
        private String transactionId;
        private Transaction.TransactionType type;
        private double amount;
        private double balanceAfter;
        private String description;
        private Transaction.TransactionStatus status;
        private String accountNumber;
        private String toAccountNumber;
        private LocalDateTime createdAt;

    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FilterRequest{
        private String accountNumber;
        private LocalDateTime fromDate;
        private LocalDateTime toDate;
        private Transaction.TransactionType type;
    }
}
