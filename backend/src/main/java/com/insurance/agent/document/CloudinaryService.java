package com.insurance.agent.document;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {
    private final String cloudName;
    private final String apiKey;
    private final String apiSecret;

    public CloudinaryService(
        @Value("${cloudinary.cloud-name:}") String cloudName,
        @Value("${cloudinary.api-key:}") String apiKey,
        @Value("${cloudinary.api-secret:}") String apiSecret
    ) {
        this.cloudName = cloudName;
        this.apiKey = apiKey;
        this.apiSecret = apiSecret;
    }

    public String upload(MultipartFile file, String folder) {
        if (file == null || file.isEmpty() || cloudName.isBlank() || apiKey.isBlank() || apiSecret.isBlank()) return null;
        try {
            Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName, "api_key", apiKey, "api_secret", apiSecret
            ));
            Map<?, ?> result = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("folder", folder, "resource_type", "auto"));
            return String.valueOf(result.get("secure_url"));
        } catch (IOException ex) {
            throw new IllegalStateException("Document upload failed", ex);
        }
    }
}

