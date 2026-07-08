package com.example.Idea.Repository;

import com.example.Idea.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.bankList")
    List<User> findAllWithBankList();
}
