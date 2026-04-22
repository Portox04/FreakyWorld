package com.freakyworld;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class StorageConfig {

    @Value("${firebase.json.path}")
    private String jsonPath;

    @Value("${firebase.json.file}")
    private String jsonFile;

    @Value("${firebase.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void inicializarFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String rutaArchivo = jsonPath + "/" + jsonFile;

            InputStream inputStream = new ClassPathResource(rutaArchivo).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .setStorageBucket(bucketName)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}