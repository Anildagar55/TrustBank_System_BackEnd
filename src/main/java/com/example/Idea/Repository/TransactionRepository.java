package com.example.Idea.Repository;

import com.example.Idea.Model.Transaction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    // Find by transaction id
    Optional<Transaction> findByTransactionId(String transactionId);

    // All transactions of an account
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.bank.accountNumber = :accountNumber
        ORDER BY t.createdAt DESC
    """)
    List<Transaction> findByAccountNumber(
            @Param("accountNumber") String accountNumber
    );

    // Date range filter
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.bank.accountNumber = :accountNumber
        AND t.createdAt BETWEEN :from AND :to
        ORDER BY t.createdAt DESC
    """)
    List<Transaction> findByAccountNumberAndDateRange(
            @Param("accountNumber") String accountNumber,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    // Type filter
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.bank.accountNumber = :accountNumber
        AND t.type = :type
        ORDER BY t.createdAt DESC
    """)
    List<Transaction> findByAccountNumberAndType(
            @Param("accountNumber") String accountNumber,
            @Param("type") Transaction.TransactionType type
    );

    // Date + Type filter
    @Query("""
        SELECT t
        FROM Transaction t
        WHERE t.bank.accountNumber = :accountNumber
        AND t.createdAt BETWEEN :from AND :to
        AND t.type = :type
        ORDER BY t.createdAt DESC
    """)
    List<Transaction> findByAccountNumberAndDateRangeAndType(
            @Param("accountNumber") String accountNumber,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("type") Transaction.TransactionType type
    );

    @Query("""
    SELECT t
    FROM Transaction t
    WHERE t.bank.accountNumber = :accNum
    ORDER BY t.createdAt DESC
    """)
    List<Transaction> findLastNTransactions(
            @Param("accNum") String accountNumber,
            Pageable pageable);
    // ── 9. check karo transaction exist karta hai ya nahi ────────
    boolean existsByTransactionId(String transactionId);
}