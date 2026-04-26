package com.keyguard.javakeyguard.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogApiService {

    @Value("${api.hmac.secret:default_secret_key}")
    private String hmacSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void sendBatch(List<EncryptedLogEntry> records) {
        if (records == null || records.isEmpty()) return;

        try {
            Map<String, Object> payloadMap = new HashMap<>();
            payloadMap.put("records", records);

            String jsonPayload = objectMapper.writeValueAsString(payloadMap);

            String timestamp = String.valueOf(System.currentTimeMillis());

            String signature = generateHmacSignature(jsonPayload, timestamp, hmacSecret);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Timestamp", timestamp);
            headers.set("X-Signature", signature);

            HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            String UPLOAD_URL = "https://java-keyguard-api.onrender.com/logs/upload";
//            String UPLOAD_URL = "http://localhost:8080/logs/upload";
            restTemplate.postForEntity(UPLOAD_URL, requestEntity, String.class);

            System.out.println("Secure batch upload successful.");
        } catch (Exception e) {
            System.err.println("Failed to send secure batch to API: " + e.getMessage());
        }
    }

    private String generateHmacSignature(String payload, String timestamp, String secret) throws Exception {
        String dataToSign = payload + timestamp;

        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        sha256_HMAC.init(secret_key);

        byte[] hash = sha256_HMAC.doFinal(dataToSign.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
}