package com.solvd.api.reqres;

public class UnSuccessfulRegistration extends Registration{

    private String error;

    public UnSuccessfulRegistration() {
    }

    public String getError() {
        return error;
    }
}
