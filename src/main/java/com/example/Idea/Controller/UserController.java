package com.example.Idea.Controller;

import com.example.Idea.Model.User;
import com.example.Idea.Service.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    UserServiceImp userService;
    @GetMapping
    public List<User> showAllUser(){
        return userService.showAll();
    }
    @GetMapping("/{id}")
    public User showUser(@PathVariable long id){
        return userService.findUser(id);
    }
    @PostMapping
    public User createUser(@RequestBody User user){
        return userService.create(user);
    }
    @PatchMapping("/{id}")
    public String updateUser(@PathVariable long id,@RequestBody User user){
        return userService.updateUser(id,user);
    }
    @DeleteMapping("/{id}")
    public String deleteUser(@PathVariable long id){
        return userService.deleteUser(id);

    }
}
