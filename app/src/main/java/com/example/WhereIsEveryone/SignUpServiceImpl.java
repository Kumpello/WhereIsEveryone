package com.example.WhereIsEveryone;


import com.vishnusivadas.advanced_httpurlconnection.PutData;

public class SignUpServiceImpl implements SignUpService{

    private final String SUCCESS_STRING = "Login Success";

    private final String IP_ADDRESS;
    private final String SIGNUP_FAILED;
    private final String CONNECTION_ERROR;

    public SignUpServiceImpl(String IP_ADDRESS, String SIGNUP_FAILED, String CONNECTION_ERROR) {
        this.IP_ADDRESS = IP_ADDRESS;
        this.SIGNUP_FAILED = SIGNUP_FAILED;
        this.CONNECTION_ERROR = CONNECTION_ERROR;
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
        PutData putData = new PutData(IP_ADDRESS, "POST", field, data);
        if (putData.startPut()) {
            if (putData.onComplete()) {
                String result = putData.getResult();
                if (result.equals(SUCCESS_STRING)) {
                    return new LoginResult(null, putData.getData());
                } else {
                    return new LoginResult(SIGNUP_FAILED, null);
                }
            }
        }
        return new LoginResult(CONNECTION_ERROR, null);
    }
}

