package com.freakyworld;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import jakarta.annotation.PostConstruct;

@Configuration
public class StorageConfig {

    @Value("${firebase.ruta-credencial}")
    private String rutaCredencial;

    @Value("${firebase.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void inicializarFirebase() throws IOException {
        if (FirebaseApp.getApps().isEmpty()) {
            String rutaArchivo = rutaCredencial.replace("classpath:", "");

            InputStream inputStream = new ClassPathResource(rutaArchivo).getInputStream();

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(inputStream))
                    .setStorageBucket(bucketName)
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }
}