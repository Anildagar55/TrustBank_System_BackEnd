package com.example.Idea.Service;

import com.example.Idea.Model.User;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface UserService {
    public User create(User user);
    public List<User>showAll();
    public String updateUser(long id,User user);
    public String deleteUser(long id);
    public User findUser(long id);
}
