package com.keyguard.javakeyguard.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EncryptedLogEntry {
    private Long id;
    private String timestamp;
    private String context;
    private String source;
    private String encryptedPayload;

    public EncryptedLogEntry() {
    }

    public EncryptedLogEntry(Long id, String timestamp, String context, String source, String encryptedPayload) {
        this.id = id;
        this.timestamp = timestamp;
        this.context = context;
        this.source = source;
        this.encryptedPayload = encryptedPayload;
    }
}