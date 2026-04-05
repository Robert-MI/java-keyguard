package com.keyguard.javakeyguard.keylogger.encryption;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;
import java.util.Properties;

public class EncryptionService {

    private static final String SECRET = loadSecret();

    private static String loadSecret() {
        try (InputStream inputStream = EncryptionService.class.getClassLoader()
                .getResourceAsStream("application.properties")) {

            if (inputStream == null) {
                throw new RuntimeException("application.properties not found");
            }

            Properties properties = new Properties();
            properties.load(inputStream);

            String secret = properties.getProperty("encryption.secret");

            if (secret == null || secret.isBlank()) {
                throw new RuntimeException("encryption.secret is missing");
            }

            if (secret.length() != 16 && secret.length() != 24 && secret.length() != 32) {
                throw new RuntimeException("encryption.secret must be 16, 24, or 32 characters long");
            }

            return secret;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load encryption secret", e);
        }
    }


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