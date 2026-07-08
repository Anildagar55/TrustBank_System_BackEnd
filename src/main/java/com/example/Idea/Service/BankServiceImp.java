package com.example.Idea.Service;

import com.example.Idea.Model.Bank;
import com.example.Idea.Model.User;
import com.example.Idea.Repository.BankRepository;
import com.example.Idea.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Idea.Controller.BankController;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Service
public class BankServiceImp implements BankService {
    //List<Bank>bankList=new ArrayList<>();
    @Autowired
    public BankRepository bankRepository;
    @Autowired
    UserRepository userRepository;
    @Override
    public List<Bank> getall() {
        return bankRepository.findAll();
    }
    @Override
    public String created(Bank bank) {
        List<Bank> bank2 = bankRepository.findByUserid(bank.getUserid());
        for (Bank bank1 : bank2) {
            if (bank1.getBanktype().equals(bank.getBanktype())) {
                return "One User only one account Same Bank..";
            }
        }
        if (!userRepository.existsById(bank.getUserid())) {
            return "User id not found..";
        }

        // ✅ userid se user fetch karo
        User user = userRepository.findById(bank.getUserid())
                .orElseThrow(() -> new RuntimeException("User not found"));
        Bank bank1 = new Bank();
        bank1.setCustomerId(generateCustomerId(bank.getBanktype()));
        bank1.setAccountNumber(generateAccountNumber(bank.getBanktype().name()));
        bank1.setBanktype(bank.getBanktype());
        bank1.setUserid(bank.getUserid());
        bank1.setUser(user);
        bank1.setHolderName(user.getName());
        bank1.setMobileNumber(user.getMobileNumber());
        bank1.setEmail(user.getEmail());
        bank1.setAmount(bank.getAmount());

        bankRepository.save(bank1);
        return "Successfully Created Bank Account...";
    }
    public String generateCustomerId(Bank.Banktype banktype) {

        String prefix = banktype.toString(); // BOB

        Long count = bankRepository.countByBanktype(banktype);

        long nextNumber = count + 1;

        return prefix + nextNumber;
    }
    public static String generateAccountNumber(String bankName){
        String s=getBankPrefix(bankName);
        Random random=new Random();
        long number = 1000000000L +
                (long)(random.nextDouble() * 9000000000L);
        return s+String.valueOf(number);
    }private static String getBankPrefix(String bankName) {

        switch (bankName.toUpperCase()) {

            case "BOB":
                return "250";

            case "UCO":
                return "0450";

            case "SBI":
                return "123";

            case "PNB":
                return "567";

            case "CANARA":
                return "123";

            default:
                return "999";
        }
    }

    @Override
    public String updated(String id, Bank bank) {
//        List<Bank>list=new ArrayList<>();
//        for (Bank bank1:bankList){
//            if(customerId==bank1.getCustomerId()){
//                bank1.setHolderName(bank.getHolderName());
//                bank1.setMobileNumber(bank.getMobileNumber());
//                return "Updated Successfully Done..";
//            }
//        }return "Customer id not found...";
        if (!bankRepository.existsById(id)){
            throw new RuntimeException("Invalid Customer id : "+id);
        }
        Bank bank1= bankRepository.findById(id).get();
        bank1.setHolderName(bank.getHolderName());
        bank1.setMobileNumber(bank.getMobileNumber());

        bankRepository.save(bank1);

        return "Updated Successfully Done..";
    }
    @Override
    public String deleted(String customerId) {
//        for (Bank bank:bankList){
//            if (customerId==bank.getCustomerId()){
//                bankList.remove(bank);
//                return "Successfully Delete..";
//            }
//        }
//        return "Customer Id not found...";
        if (!bankRepository.existsById(customerId)){
            throw new RuntimeException("Invalid Customer id : "+customerId);

        }
        bankRepository.deleteById(customerId);
        return "Successfully Delete..";
    }

    @Override
    public List<Bank> oneUserDetails(long user_id) {
        if (!bankRepository.existsByUserid(user_id)){
            return null;
        }
        return bankRepository.findByUserid(user_id);
    }
}
