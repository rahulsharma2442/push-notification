package com.example.officenotification;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;

@Component
public class FirebaseInitializer {

    @PostConstruct
    public void init() throws Exception {
        FileInputStream serviceAccount =
                new FileInputStream("ServiceAccountKey.json"); // adjust path

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized");
        }
    }
}
