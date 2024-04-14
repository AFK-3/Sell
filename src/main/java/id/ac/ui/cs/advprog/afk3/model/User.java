package id.ac.ui.cs.advprog.afk3.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Setter @Getter
public class User{
    private String username;
    private String password;
    private String name;
    private String address;
    private String phoneNumber;
    private String type;
}