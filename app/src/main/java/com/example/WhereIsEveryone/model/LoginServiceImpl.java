package com.example.WhereIsEveryone.model;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class LoginServiceImpl implements LoginService {

    // TODO(kumpel): Let's use FirebaseLogin instead of hindi-server

    // TODO(kumpel): This may be just a static field
    //               As it's an internal login implementation
    //               And it shouldn't change between version
    private static final String SUCCESS_STRING = "TODO";

    private final String ipAddress;

    public LoginServiceImpl(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public LoginResult login(String login, String password) {
        String[] field = new String[2];
        field[0] = "username";
        field[1] = "password";
        String[] data = new String[2];
        data[0] = login;
        data[1] = password;

        PutData putData = new PutData(ipAddress, "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals(SUCCESS_STRING)) {
                    return new LoginResult(null, putData.getData());
                } else {
                    return new LoginResult(LoginResult.Error.LoginFailed, null);
                }
            }
        }
        return new LoginResult(LoginResult.Error.ConnectionError, null);
    }

    @Override
    public LoginResult signUp(String login, String password, String email) {
        String[] field = new String[3];
        field[0] = "username";
        field[1] = "password";
        field[2] = "email";
        String[] data = new String[3];
        data[0] = login;
        data[1] = password;
        data[2] = email;
        PutData putData = new PutData(ipAddress, "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals(SUCCESS_STRING)) {
                    return new LoginResult(null, putData.getData());
                } else {
                    return new LoginResult(LoginResult.Error.SignUpFailed, null);
                }
            }
        }
        return new LoginResult(LoginResult.Error.ConnectionError, null);
    }

}
