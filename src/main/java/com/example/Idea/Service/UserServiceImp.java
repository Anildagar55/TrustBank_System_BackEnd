package com.example.Idea.Service;

import com.example.Idea.Model.Bank;
import com.example.Idea.Model.User;
import com.example.Idea.Repository.BankRepository;
import com.example.Idea.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class UserServiceImp implements UserService{
    @Autowired
    UserRepository userRepository;
    @Autowired
    BankRepository bankRepository;
    @Override
    public User create(User user) {
       return userRepository.save(user);
//        return "Successfully add user";
    }

    @Override
    public List<User> showAll() {
        return userRepository.findAllWithBankList();
    }

    @Override
    public String updateUser(long id, User user) {
        if (!userRepository.existsById(id)){
            return "User id not found..";
        }
        User user2=userRepository.findById(id).get();
        List<Bank> bank=bankRepository.findByUserid(id);
        for (Bank bank1:bank){
            bank1.setHolderName(user.getName());
            bank1.setEmail(user.getEmail());
            bank1.setMobileNumber(user.getMobileNumber());
            bankRepository.save(bank1);
        }
        user2.setName(user.getName());
//        user2.setAadhar(user.getAadhar());
        user2.setEmail(user.getEmail());
        user2.setMobileNumber(user.getMobileNumber());
        userRepository.save(user2);

        return "Updata data successfully..";
    }

    @Override
    public String deleteUser(long id) {
        if (!userRepository.existsById(id)){
            return "User id not found.";
        }
        userRepository.deleteById(id);
        return "Delete successfully...";
    }

    @Override
    public User findUser(long id) {
        if (!userRepository.existsById(id)){
            throw new RuntimeException("User id not found "+id);
         }

        return userRepository.findById(id).get();
    }
}
