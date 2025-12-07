package com.example.officenotification;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class FirebaseInitializer {

    @PostConstruct
    public void init() throws Exception {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        // Get environment variables
        String type = dotenv.get("FIREBASE_TYPE");
        String projectId = dotenv.get("FIREBASE_PROJECT_ID");
        String privateKeyId = dotenv.get("FIREBASE_PRIVATE_KEY_ID");
        String privateKeyBase64 = dotenv.get("FIREBASE_PRIVATE_KEY_BASE64");
        String clientEmail = dotenv.get("FIREBASE_CLIENT_EMAIL");
        String clientId = dotenv.get("FIREBASE_CLIENT_ID");
        String authUri = dotenv.get("FIREBASE_AUTH_URI");
        String tokenUri = dotenv.get("FIREBASE_TOKEN_URI");
        String authProviderCertUrl = dotenv.get("FIREBASE_AUTH_PROVIDER_CERT_URL");
        String clientCertUrl = dotenv.get("FIREBASE_CLIENT_CERT_URL");
        String universeDomain = dotenv.get("FIREBASE_UNIVERSE_DOMAIN");

        // Decode base64 private key
        String privateKey = new String(Base64.getDecoder().decode(privateKeyBase64), StandardCharsets.UTF_8);

        // Build JSON string for service account
        String serviceAccountJson = String.format(
            "{"
            + "\"type\": \"%s\","
            + "\"project_id\": \"%s\","
            + "\"private_key_id\": \"%s\","
            + "\"private_key\": %s,"
            + "\"client_email\": \"%s\","
            + "\"client_id\": \"%s\","
            + "\"auth_uri\": \"%s\","
            + "\"token_uri\": \"%s\","
            + "\"auth_provider_x509_cert_url\": \"%s\","
            + "\"client_x509_cert_url\": \"%s\","
            + "\"universe_domain\": \"%s\""
            + "}",
            type, projectId, privateKeyId, privateKey, clientEmail, clientId,
            authUri, tokenUri, authProviderCertUrl, clientCertUrl, universeDomain
        );

        InputStream serviceAccountStream = new ByteArrayInputStream(
            serviceAccountJson.getBytes(StandardCharsets.UTF_8)
        );

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();

        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            System.out.println("Firebase initialized successfully");
        }
    }
}
