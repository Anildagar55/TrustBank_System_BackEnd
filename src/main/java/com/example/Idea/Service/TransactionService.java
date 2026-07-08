package com.example.Idea.Service;

import com.example.Idea.DTO.TransactionDTO;
import com.example.Idea.Model.Transaction;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface TransactionService {
    TransactionDTO.TransactionResponse deposit(TransactionDTO.DepositRequest request);
    TransactionDTO.TransactionResponse withdraw(TransactionDTO.WithdrawRequest request);
    TransactionDTO.TransactionResponse transfer(TransactionDTO.TransferRequest request);

//    Quesry operation
    TransactionDTO.TransactionResponse getTransactionById(String transactionId);
    List<TransactionDTO.TransactionResponse>getTransacionHistory(String accountNumber);
    List<TransactionDTO.TransactionResponse>getFilteredTransactions(TransactionDTO.FilterRequest request);
List<TransactionDTO.TransactionResponse>getallTransaction();
public List<Transaction>showAll();
List<TransactionDTO.TransactionResponse>getNTransaction(String accountNumber,int limit);

}
