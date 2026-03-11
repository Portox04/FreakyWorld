package com.freakyworld.service;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.firebase.cloud.StorageClient;
import java.io.IOException;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FirebaseStorageService {

    public String cargarImagen(MultipartFile archivo) throws IOException {
        if (archivo == null || archivo.isEmpty()) {
            return null;
        }

        String nombreOriginal = archivo.getOriginalFilename();
        String extension = "";

        if (nombreOriginal != null && nombreOriginal.contains(".")) {
            extension = nombreOriginal.substring(nombreOriginal.lastIndexOf("."));
        }

        String nombreArchivo = UUID.randomUUID().toString() + extension;

        Bucket bucket = StorageClient.getInstance().bucket();

        Blob blob = bucket.create(
                nombreArchivo,
                archivo.getBytes(),
                archivo.getContentType()
        );

        return blob.getMediaLink();
    }

    public boolean eliminarArchivo(String nombreArchivo) {
        try {
            Bucket bucket = StorageClient.getInstance().bucket();
            Blob blob = bucket.get(nombreArchivo);

            if (blob != null) {
                return blob.delete();
            }

            return false;
        } catch (Exception e) {
            return false;
        }
    }
}
