package com.example.Idea.Controller;

import com.example.Idea.Model.Bank;
import com.example.Idea.Model.User;
import com.example.Idea.Service.BankService;
import com.example.Idea.Service.BankServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankController {
//    BankService bankService=new BankServiceImp();
    @Autowired
    BankServiceImp bankService;
    @GetMapping
    public ResponseEntity<?>getAllDetails(){
        return ResponseEntity.ok(bankService.getall());
    }
    @GetMapping("/{userid}")
    public ResponseEntity<?>oneUserDetails(@PathVariable long userid){
        if (bankService.oneUserDetails(userid)==null){
            return ResponseEntity.badRequest().body("user id not found..");
        }
        return ResponseEntity.ok(bankService.oneUserDetails(userid));
    }
    @PostMapping
    public ResponseEntity<?> created(@RequestBody Bank bank){

        return ResponseEntity.status(HttpStatus.CREATED).body(bankService.created(bank));
    }
    @PutMapping("/{customerId}")
    public ResponseEntity<?> updatedDetails(@PathVariable String customerId,@RequestBody Bank bank){
        try{
            return ResponseEntity.ok(bankService.updated(customerId,bank));
        }catch (Exception ex){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
        }
    }
    @DeleteMapping("/{customerId}")
    public ResponseEntity<?> deleteDetails(@PathVariable String customerId){
        try {
            bankService.deleted(customerId);
            return ResponseEntity.noContent().build();
        }
        catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
