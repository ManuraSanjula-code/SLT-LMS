package com.slt.peotv.userservice.lms.shared.Messaging;

import com.slt.peotv.userservice.lms.entity.UserEntity;

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

