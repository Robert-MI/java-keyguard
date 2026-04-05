package com.keyguard.javakeyguard.keylogger.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public class EncryptionService {

    private static final String SECRET = "1234567890123456";

    private SecretKey getKey() {
        return new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "AES");
    }

    public String encrypt(String plainText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, getKey());
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    public String decrypt(String encryptedText) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, getKey());
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }

    public void printDecryptedLogFile(String logFilePath) {
        ObjectMapper objectMapper = new ObjectMapper();
        Path path = Path.of(logFilePath);

        if (!Files.exists(path)) {
            System.out.println("No log file found to decrypt.");
            return;
        }

        try {
            String finalResult = "";

            for (String line : Files.readAllLines(path)) {
                if (line.isBlank()) {
                    continue;
                }

                EncryptedLogEntry entry = objectMapper.readValue(line, EncryptedLogEntry.class);
                finalResult = decrypt(entry.getEncryptedPayload());
            }

            System.out.println("Final decrypted result:");
            System.out.println(finalResult.isBlank() ? "[empty]" : finalResult);

        } catch (IOException e) {
            throw new RuntimeException("Failed to read encrypted log file", e);
        }
    }
}