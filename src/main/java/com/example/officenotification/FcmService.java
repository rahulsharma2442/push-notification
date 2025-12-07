package com.example.officenotification;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class FcmService {

    private static final Logger logger = LoggerFactory.getLogger(FcmService.class);

    public String sendToToken(String token, String title, String body) throws FirebaseMessagingException {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be null or empty");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (body == null || body.trim().isEmpty()) {
            throw new IllegalArgumentException("Body cannot be null or empty");
        }

        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder().setTitle(title).setBody(body).build())
            .build();

        try {
            String response = FirebaseMessaging.getInstance().send(message);
            logger.info("Successfully sent message: {}", response);
            return response;
        } catch (FirebaseMessagingException e) {
            logger.error("Failed to send message to token: {}", token, e);
            throw e;
        }
    }
}

