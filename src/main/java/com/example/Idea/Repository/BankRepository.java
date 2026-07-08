package com.example.Idea.Repository;

import com.example.Idea.Model.Bank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankRepository extends JpaRepository<Bank,String> {
    Optional<Bank> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    long countByBanktype(Bank.Banktype banktype);
     List<Bank>findByUserid(long userid);
     boolean existsByUserid(long userid);
}
