package com.slt.peotv.lmsmangmentservice.utils.Service;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    @JmsListener(destination = "user.updates.queue")
    public void receiveUserUpdate(UserMessage message) {
        System.out.println("Received update: " + message.getAction() + " for user " + message.getUser().getEmail());
    }
}
