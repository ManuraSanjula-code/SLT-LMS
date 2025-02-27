package com.slt.peotv.userservice.lms.shared.Messaging;

import com.slt.peotv.userservice.lms.entity.UserEntity;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserEventPublisher {

    private final JmsTemplate jmsTemplate;

    public UserEventPublisher(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    public void sendUserUpdate(UserEntity user, String action) {
        UserMessage message = new UserMessage(user, action);
        jmsTemplate.convertAndSend("user.updates.queue", message);
    }
}
