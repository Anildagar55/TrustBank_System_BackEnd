package com.example.Idea.Service;

import com.example.Idea.Model.Bank;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface BankService {
    public List<Bank>getall();
    public  String created(Bank bank);
    public String updated(String customerId,Bank bank);
    public String deleted(String customerId);
    public List<Bank>oneUserDetails(long user_id);
}
