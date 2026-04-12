package com.keyguard.javakeyguard.keylogger.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@Service
public class LogStorageService {

    private static final String LOG_FILE_PATH = "logs/encrypted-log.ndjson";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void save(EncryptedLogEntry entry) {
        try {
            File file = new File(LOG_FILE_PATH);
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter writer = new FileWriter(file, true)) {
                writer.write(objectMapper.writeValueAsString(entry));
                writer.write(System.lineSeparator());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to save log entry", e);
        }
    }
}