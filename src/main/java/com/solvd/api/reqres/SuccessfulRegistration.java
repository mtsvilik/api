package com.solvd.api.reqres;

public class SuccessfulRegistration extends Registration {

    private Integer id;
    private String token;

    public SuccessfulRegistration() {
    }

    public Integer getId() {
        return id;
    }

    public String getToken() {
        return token;
    }
}
