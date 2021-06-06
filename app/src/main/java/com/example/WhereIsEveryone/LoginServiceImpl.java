package com.example.WhereIsEveryone;

import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class LoginServiceImpl implements LoginService {

    private final String SUCCESS_STRING = "Login Success";
    private final String IP_ADDRESS;
    private final String LOGIN_FAILED;
    private final String CONNECTION_ERROR;

    public LoginServiceImpl(String IP_ADDRESS, String LOGIN_FAILED, String CONNECTION_ERROR) {
        this.IP_ADDRESS = IP_ADDRESS;
        this.LOGIN_FAILED = LOGIN_FAILED;
        this.CONNECTION_ERROR = CONNECTION_ERROR;
    }

    @Override
    public LoginResult login(String login, String password) {
        String[] field = new String[2];
        field[0] = "username";
        field[1] = "password";
        String[] data = new String[2];
        data[0] = login;
        data[1] = password;
        PutData putData = new PutData(IP_ADDRESS, "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals(SUCCESS_STRING)) {
                    return new LoginResult(null, putData.getData());
                } else {
                    return new LoginResult(LOGIN_FAILED, null);
                }
            }
        }
        return new LoginResult(CONNECTION_ERROR, null);
    }


}
