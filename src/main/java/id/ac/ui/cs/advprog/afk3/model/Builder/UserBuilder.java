package id.ac.ui.cs.advprog.afk3.model.Builder;


import id.ac.ui.cs.advprog.afk3.model.Enum.UserType;
import id.ac.ui.cs.advprog.afk3.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserBuilder {
    private User currentUser;
    public UserBuilder(){
        this.reset();
    }

    public UserBuilder reset(){
        currentUser = new User();
        return this;
    }

    public UserBuilder addName(String name){
        currentUser.setName(name);
        return this;
    }

    public UserBuilder addUsername(String username){
        currentUser.setUsername(username);
        return this;
    }

    public UserBuilder addPassword(String password){
        currentUser.setPassword(password);
        return this;
    }

    public UserBuilder addAddress(String address){
        currentUser.setAddress(address);
        return this;
    }

    public UserBuilder addPhoneNumber(String phoneNumber){
        currentUser.setPhoneNumber(phoneNumber);
        return this;
    }

    public UserBuilder addType(String type){
        if (UserType.contains(type)){
            currentUser.setType(type);
            return this;
        }else{
            throw new IllegalArgumentException();
        }
    }

    public UserBuilder setCurrent(User user){
        currentUser = user;
        return this;
    }

    public User build(){
        return currentUser;
    }
}
