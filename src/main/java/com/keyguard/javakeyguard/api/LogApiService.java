package com.keyguard.javakeyguard.api;

import com.keyguard.javakeyguard.model.EncryptedLogEntry;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LogApiService {

    private final RestTemplate restTemplate = new RestTemplate();

    public void sendBatch(List<EncryptedLogEntry> records) {
        if (records == null || records.isEmpty()) return;

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("records", records);

            String UPLOAD_URL = "https://java-keyguard-api.onrender.com/logs/upload";
            restTemplate.postForEntity(UPLOAD_URL, requestBody, String.class);
            System.out.println("Batch upload successful. Sent " + records.size() + " records.");
        } catch (Exception e) {
            System.err.println("Failed to send batch to API: " + e.getMessage());
        }
    }
}
