package com.example.WhereIsEveryone;

import android.app.Activity;

public class DependencyContainer {

    public static DependencyContainer getContainer() {
        return new DependencyContainer();
    }

    public LoginService getLoginService(String IP_ADDRESS, String LOGIN_FAILED, String CONNECTION_ERROR) {
        return new LoginServiceImpl(IP_ADDRESS, LOGIN_FAILED, CONNECTION_ERROR);
    }

    public UserService getUserService() {
        return new UserServiceImpl();
    }

    public LoginPresenter getLoginPresenter(LoginView view, String IP_ADDRESS, String LOGIN_FAILED, String CONNECTION_ERROR) {
        return new LoginPresenterImpl(view, getLoginService(IP_ADDRESS, LOGIN_FAILED, CONNECTION_ERROR), getUserService());
    }

    public PermissionHandler getPermissionHandler(Activity activity) {
        return new PermissionHandlerImpl(activity);
    }

    public SignUpService getSignUpService(String IP_ADDRESS, String LOGIN_FAILED, String CONNECTION_ERROR) {
        return new SignUpServiceImpl(IP_ADDRESS, LOGIN_FAILED, CONNECTION_ERROR);
    }

    public SignUpPresenter getSignUpPresenter(LoginView view, String IP_ADDRESS, String LOGIN_FAILED, String CONNECTION_ERROR) {
        return new SignUpPresenterImpl(view, getSignUpService(IP_ADDRESS, LOGIN_FAILED, CONNECTION_ERROR));
    }
}
