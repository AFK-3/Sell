package id.ac.ui.cs.advprog.afk3.service;

import id.ac.ui.cs.advprog.afk3.model.User;
import id.ac.ui.cs.advprog.afk3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;

    @Override
    public User create(User user){
        if(userRepository.findByUsername(user.getUsername())==null &&
            fieldValid(user)){
            userRepository.createUser(user);
            return user;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public boolean fieldValid(User user){
        boolean phoneIsNumber = true;
        for (Character c : user.getPhoneNumber().toCharArray()){
            if(!Character.isDigit(c)){
                phoneIsNumber=false;
                break;
            }
        }
        return !user.getAddress().isEmpty() && phoneIsNumber;
    }

    @Override
    public List<User> findAll(){
        Iterator<User> userIterator=userRepository.findAll();
        List<User> allUser = new ArrayList<>();
        userIterator.forEachRemaining(allUser::add);
        return allUser;
    }

    @Override
    public User findByUsername(String username){
        return userRepository.findByUsername(username);
    }

    @Override
    public void update (String userId, User user){
        userRepository.update(userId, user);
    }

    @Override
    public void deleteUserById(String userId) {
        userRepository.delete(userId);
    }
}
