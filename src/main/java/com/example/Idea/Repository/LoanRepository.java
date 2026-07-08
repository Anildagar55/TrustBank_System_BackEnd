package com.example.Idea.Repository;

import com.example.Idea.Model.Loan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface LoanRepository extends JpaRepository<Loan,Long> {
    Optional<Loan> findByLoanNumber(String loanNumber);
    List<Loan>findByStatus(Loan.LoanStatus status);
    @Query("""
SELECT l
FROM Loan l
WHERE l.status='ACTIVE'
AND l.nextEmiDate <= :now
""")
    List<Loan> findLoansWithEmiDue(@Param("now") LocalDateTime now);

    boolean existsByLoanNumber(String loanNumber);
    @Query("SELECT l FROM Loan l WHERE l.status = 'PENDING' " +
            "ORDER BY l.createdAt ASC")
    List<Loan> findPendingLoans();

    List<Loan>findByUserId(Long userId);
}
