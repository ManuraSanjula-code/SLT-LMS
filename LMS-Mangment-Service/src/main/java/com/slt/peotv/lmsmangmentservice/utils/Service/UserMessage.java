package com.slt.peotv.lmsmangmentservice.utils.Service;

import com.slt.peotv.lmsmangmentservice.entity.User.UserEntity;

import java.io.Serializable;

public class UserMessage implements Serializable {

    private static final long serialVersionUID = 4444433441235894403L;

    private UserEntity user;
    private String action; // INSERT, UPDATE, DELETE

    public UserMessage() {}

    public UserMessage(UserEntity user, String action) {
        this.user = user;
        this.action = action;
    }

    public UserEntity getUser() {
        return user;
    }

    public String getAction() {
        return action;
    }
}

