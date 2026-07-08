package com.example.Idea.Controller;

import com.example.Idea.DTO.TransactionDTO;
import com.example.Idea.Model.Transaction;
import com.example.Idea.Repository.TransactionRepository;
import com.example.Idea.Service.TransactionService;
import com.example.Idea.Service.TransactionServiceImp;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor

public class TransactionController {

    @Autowired
    private  TransactionService transactionService;
    @Autowired
 private TransactionServiceImp transactionServiceImp;
    @PostMapping("/deposit")
    public ResponseEntity<?>deposit(@RequestBody TransactionDTO.DepositRequest request){
        TransactionDTO.TransactionResponse response=transactionService.deposit(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?>withdraw(@RequestBody TransactionDTO.WithdrawRequest request){
        TransactionDTO.TransactionResponse response=transactionService.withdraw(request);
        return ResponseEntity.ok(response);
    }
    @PostMapping("/transfer")
    public ResponseEntity<?>transfer(@RequestBody TransactionDTO.TransferRequest request){
        TransactionDTO.TransactionResponse response=transactionService.transfer(request);
       return ResponseEntity.ok(response);
    }
    @GetMapping("/{transactionId}")
    public ResponseEntity<?>getById(@PathVariable String transactionId){
        TransactionDTO.TransactionResponse response=transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
    }
 @GetMapping
    public ResponseEntity<?>getall(){
        return ResponseEntity.ok(transactionService.getallTransaction());
 }
 @GetMapping("/al")
    public ResponseEntity<?>showAll(){
        return ResponseEntity.ok(transactionServiceImp.showAll());
 }
 @GetMapping("/transaction/{transactionId}")
    public ResponseEntity<TransactionDTO.TransactionResponse>getTransactionByIdShow(@PathVariable String transactionId){
        TransactionDTO.TransactionResponse response=transactionService.getTransactionById(transactionId);
        return ResponseEntity.ok(response);
 }
 @GetMapping("/Number/{accountNumber}")
    public ResponseEntity<?>getTransacionHistoryshow(@PathVariable String accountNumber){
       List<TransactionDTO.TransactionResponse> response=transactionService.getTransacionHistory(accountNumber);
       return ResponseEntity.ok(response);
 }
 @GetMapping("/all")
    public ResponseEntity<?>getAlltransaction(){
        return ResponseEntity.ok(transactionService.getallTransaction());
 }
@PostMapping("/filter")
    public ResponseEntity<?>getfiltertransaction(@RequestBody TransactionDTO.FilterRequest request){
    List<TransactionDTO.TransactionResponse> response =
            transactionService.getFilteredTransactions(request);
        return ResponseEntity.ok(response);
}
@GetMapping("/ntransaction/{accountNumber}/{limit}")
    public ResponseEntity<?>getNTransaction(@PathVariable String accountNumber,@PathVariable int limit){
return ResponseEntity.ok(transactionService.getNTransaction(accountNumber,limit));
}
}