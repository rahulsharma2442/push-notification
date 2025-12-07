package com.example.officenotification;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class TokenController {

    private final ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();
    private final FcmService fcmService;

    public TokenController(FcmService fcmService) {
        this.fcmService = fcmService;
    }

    @PostMapping("/register-token")
    public ResponseEntity<String> registerToken(@RequestBody TokenRequest req) {
        tokens.put(req.getUserId(), req.getToken());
        return ResponseEntity.ok("token registered");
    }

    @PostMapping("/send")
    public ResponseEntity<String> send(@RequestBody SendRequest req) throws Exception {
        String token = tokens.get(req.getUserId());
        if (token == null) {
            return ResponseEntity.badRequest().body("no token for user");
        }
        String msgId = fcmService.sendToToken(token, req.getTitle(), req.getBody());
        return ResponseEntity.ok("sent: " + msgId);
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
