package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.User;
import org.springframework.stereotype.Service;

import java.util.List;

public interface UserService{
    public User create(User user);
    public List<User> findAll();
    User findByUsername(String username);
    public void update(String userId, User user);
    public void deleteUserById(String userId);
}
