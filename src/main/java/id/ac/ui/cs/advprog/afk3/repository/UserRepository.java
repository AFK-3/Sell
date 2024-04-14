package id.ac.ui.cs.advprog.afk3.repository;

import id.ac.ui.cs.advprog.afk3.model.Builder.UserBuilder;
import id.ac.ui.cs.advprog.afk3.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Repository
public class UserRepository {
    private final List<User> userData = new ArrayList<>();

    @Autowired
    private UserBuilder userBuilder;
    public User createUser(User newUser){
        userData.add(newUser);
        return newUser;
    }
    public Iterator<User> findAll(){
        return userData.iterator();
    }
    public User findByUsername(String username){
        for (User User: userData){
            if (User.getUsername().equals(username)){
                return User;
            }
        }
        return null;
    }

    public User update(String username, User updatedUser){
        for (int i=0; i<userData.size(); i++){
            User user = userData.get(i);
            if(user.getUsername().equals(username)){
                User newUser = userBuilder.reset()
                        .setCurrent(updatedUser)
                        .addUsername(username)
                        .addPassword(user.getPassword())
                        .build();
                userData.remove(i);
                userData.add(i,newUser);
                return newUser;
            }
        }
        return null;
    }

    public void delete(String username){
        userData.removeIf(User -> User.getUsername().equals(username));
    }
}
