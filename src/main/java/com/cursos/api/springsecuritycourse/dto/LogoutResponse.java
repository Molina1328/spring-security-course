package com.cursos.api.springsecuritycourse.dto;

import java.io.Serializable;

public class LogoutResponse implements Serializable {

    private String message;

    public LogoutResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

