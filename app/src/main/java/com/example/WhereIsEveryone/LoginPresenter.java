package com.example.WhereIsEveryone;

public interface LoginPresenter {
    void loginButtonClicked(String login, String password);
    UserService getUserService();
}
