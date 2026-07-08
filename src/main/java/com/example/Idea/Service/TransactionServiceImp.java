package com.example.Idea.Service;

import com.example.Idea.DTO.TransactionDTO;
import com.example.Idea.ExceptionHandle.InsufficientBalanceException;
import com.example.Idea.ExceptionHandle.InvalidAccountNumberException;
import com.example.Idea.Model.Bank;
import com.example.Idea.Model.Transaction;
import com.example.Idea.Randomgenerated.NumberGenerator;
import com.example.Idea.Repository.BankRepository;
import com.example.Idea.Repository.TransactionRepository;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.naming.InsufficientResourcesException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionServiceImp implements TransactionService {

    @Autowired
    private  BankRepository bankRepository;
    @Autowired
     TransactionRepository transactionRepository;

   @Transactional
    @Override
    public TransactionDTO.TransactionResponse deposit(TransactionDTO.DepositRequest request) {
       if (!bankRepository.existsByAccountNumber(request.getAccountNumber())){
           throw new InvalidAccountNumberException("Account not found : "+request.getAccountNumber());
       }
       Bank bank=bankRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow();
       bank.setAmount(bank.getAmount()+request.getAmount());
       bankRepository.save(bank);
LocalDateTime created=LocalDateTime.now();
       Transaction txn=buildAndSave(bank,Transaction.TransactionType.DEPOSIT,
               request.getAmount(),
               bank.getAmount(),
               request.getDescription(),
               null,created);
       return mapToResponse(txn);
    }

    @Override
    @Transactional
    public TransactionDTO.TransactionResponse withdraw(TransactionDTO.WithdrawRequest request) {
        if (!bankRepository.existsByAccountNumber(request.getAccountNumber())){
            throw new InvalidAccountNumberException("Account not found");
        }
        Bank bank=bankRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow();
        if (bank.getAmount()<request.getAmount()){
            throw new InsufficientBalanceException("Insufficient balance available : "+bank.getAmount()
                    +" | Requested : "+request.getAmount());
        }
        bank.setAmount(bank.getAmount()-request.getAmount());
        bankRepository.save(bank);
LocalDateTime created=LocalDateTime.now();
        Transaction txn=buildAndSave(bank,Transaction.TransactionType.WITHDRAWAL
                                  ,request.getAmount(),
                                 bank.getAmount(),
                                 request.getDescription(),null,created);
       return mapToResponse(txn);
    }

    @Override
    @Transactional
    public TransactionDTO.TransactionResponse transfer(TransactionDTO.TransferRequest request) {
       if (request.getFromAccountNumber().equals(request.getToAccountNumber())){
           throw new RuntimeException("From aur To account same nahi ho sakte");
       }
       if (!bankRepository.existsByAccountNumber(request.getToAccountNumber())){
           throw new InvalidAccountNumberException("Please Enter Corret Account Number.");
       }
       Bank fromAccount=bankRepository.findByAccountNumber(request.getFromAccountNumber()).orElseThrow();
       Bank toAccount=bankRepository.findByAccountNumber(request.getToAccountNumber()).orElseThrow();
       if (fromAccount==null || toAccount==null){
           throw new InvalidAccountNumberException("account not found");
       }
        if (fromAccount.getAmount() < request.getAmount()) {
            throw new InsufficientBalanceException("Insufficient balance. Available balance : "+fromAccount.getAmount());
        }
       String desc=request.getDescription() !=null ? request.getDescription() :"Fund Transfer";

       fromAccount.setAmount(fromAccount.getAmount()-request.getAmount());
       bankRepository.save(fromAccount);

       toAccount.setAmount(toAccount.getAmount()+request.getAmount());
       bankRepository.save(toAccount);
LocalDateTime created=LocalDateTime.now();
       Transaction debitTxn=buildAndSave(fromAccount,
                                         Transaction.TransactionType.TRANSFER_DEBIT,
                                         request.getAmount(),
                                         fromAccount.getAmount(),
                                         desc,request.getToAccountNumber(),created);
        buildAndSave(
                  toAccount,
                Transaction.TransactionType.TRANSFER_CREDIT,
                request.getAmount(),
                toAccount.getAmount(),
                desc,
                request.getFromAccountNumber(),created
        ) ;
       return mapToResponse(debitTxn);
    }

    @Override
    public TransactionDTO.TransactionResponse getTransactionById(String transactionId) {
       Transaction txn=transactionRepository.findByTransactionId(transactionId)
               .orElseThrow(()->new RuntimeException("Transaction not found :"+transactionId));

        return mapToResponse(txn);
    }

    @Override
    public List<TransactionDTO.TransactionResponse> getTransacionHistory(String accountNumber) {
        return transactionRepository.findByAccountNumber(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    public List<TransactionDTO.TransactionResponse>getallTransaction(){
       return transactionRepository.findAll()
               .stream()
               .map(this::mapToResponse)
               .toList();
    }

    @Override
    public List<Transaction> showAll() {
        return transactionRepository.findAll();
    }

    @Override
    public List<TransactionDTO.TransactionResponse> getNTransaction(String accountNumber, int limit) {

        Pageable pageable = PageRequest.of(0, limit);

        return transactionRepository
                .findLastNTransactions(accountNumber, pageable)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<TransactionDTO.TransactionResponse> getFilteredTransactions(
            TransactionDTO.FilterRequest request) {

        Bank bank = bankRepository.findByAccountNumber(request.getAccountNumber()).orElseThrow();
        System.out.println(bank.getAccountNumber());
        if (bank == null) {
            throw new InvalidAccountNumberException(
                    "Account not found : " + request.getAccountNumber());
        }
        List<Transaction> transactions;

        if (request.getFromDate() != null &&
                request.getToDate() != null &&
                request.getType() != null) {

            transactions = transactionRepository
                    .findByAccountNumberAndDateRangeAndType(
                            bank.getAccountNumber(),
                            request.getFromDate(),
                            request.getToDate(),
                            request.getType());

        } else if (request.getFromDate() != null &&
                request.getToDate() != null) {

            transactions = transactionRepository
                    .findByAccountNumberAndDateRange(
                            bank.getAccountNumber(),
                            request.getFromDate(),
                            request.getToDate());
            System.out.println(transactions);
        } else if (request.getType() != null) {

            transactions = transactionRepository
                    .findByAccountNumberAndType(
                            bank.getAccountNumber(),
                            request.getType());
        } else {
            transactions = transactionRepository
                    .findByAccountNumber(bank.getAccountNumber());
        }
        System.out.println("Account Number = " + request.getAccountNumber());
        System.out.println("Type = " + request.getType());
        System.out.println("From = " + request.getFromDate());
        System.out.println("To = " + request.getToDate());

        return transactions.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    NumberGenerator ob=new NumberGenerator();
   private Transaction buildAndSave(Bank bank, Transaction.TransactionType type
           , double amount, double balanceAfter, String description, String toAccountNumber, LocalDateTime created){
       Transaction txn=Transaction.builder().transactionId(ob.generateTransactionId())
               .type(type)
               .amount(amount)
               .balanceAfter(balanceAfter)
               .description(description)
               .status(Transaction.TransactionStatus.SUCCESS)
               .bank(bank)
               .toAccountNumber(toAccountNumber)
               .createdAt(created)
               .build();
  return transactionRepository.save(txn);
   }

    private TransactionDTO.TransactionResponse mapToResponse(Transaction t){
       return TransactionDTO.TransactionResponse.builder()
               .id(t.getId())
               .transactionId(t.getTransactionId())
               .type(t.getType())
               .amount(t.getAmount())
               .balanceAfter(t.getBalanceAfter())
               .description(t.getDescription())
               .status(t.getStatus())
               .accountNumber(t.getBank().getAccountNumber())
               .toAccountNumber(t.getToAccountNumber())
               .createdAt(t.getCreatedAt())
               .build();
    }
}
