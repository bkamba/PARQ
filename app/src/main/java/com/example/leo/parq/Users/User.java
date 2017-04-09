package com.example.leo.parq.Users;
import java.util.*;
/**
 * Created by cmaso on 4/8/2017.
 */

public abstract class User {
    private String firstName, lastName, phone, email, username, password, avatar;
    private long ID;
    private Random random_gen = new Random();

    public User(String firstName, String lastName, String phone, String email,
                String username, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.username = username;
        this.password = password; //SHOULD THIS BE KEPT HERE?
        ID = random_gen.nextLong();
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getAvatar() {
        return this.avatar;
    }

    public void setAvatar(String img) {
        this.avatar = img;
    }
}
