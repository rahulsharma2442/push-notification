package com.example.officenotification;

import com.google.firebase.messaging.FirebaseMessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class TokenController {

    private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
    private final FcmService fcmService;

    public TokenController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/register-token")
    public ResponseEntity<String> registerToken(@RequestBody TokenRequest req) {
        try {
            if (req.getUserId() == null || req.getUserId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("userId is required");
            }
            if (req.getToken() == null || req.getToken().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("token is required");
            }
            
            tokens.put(req.getUserId(), req.getToken());
            logger.info("Token registered for user: {}", req.getUserId());
            return ResponseEntity.ok("Token registered successfully");
        } catch (Exception e) {
            logger.error("Error registering token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to register token");
        }
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody SendRequest req) {
        try {
            if (req.getUserId() == null || req.getUserId().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("userId is required");
            }
            if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("title is required");
            }
            if (req.getBody() == null || req.getBody().trim().isEmpty()) {
                return ResponseEntity.badRequest().body("body is required");
            }

            String token = tokens.get(req.getUserId());
            if (token == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No token found for user: " + req.getUserId());
            }
            
            String msgId = fcmService.sendToToken(token, req.getTitle(), req.getBody());
            logger.info("Notification sent to user: {}, messageId: {}", req.getUserId(), msgId);
            return ResponseEntity.ok("Notification sent successfully. MessageId: " + msgId);
        } catch (FirebaseMessagingException e) {
            logger.error("Firebase messaging error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send notification: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error sending notification", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to send notification");
        }
    }

    public static class TokenRequest {
        private String userId;
        private String token;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    public static class SendRequest {
        private String userId;
        private String title;
        private String body;
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getBody() { return body; }
        public void setBody(String body) { this.body = body; }
    }
}
