package com.example.WhereIsEveryone;

public class UserServiceImpl implements UserService {
    String userID;

    public UserServiceImpl() {
        this.userID = null;
    }

    @Override
    public void saveToken(String token) {
        userID = token;
    }

    @Override
    public String getToken() {
        return userID;
    }
}
