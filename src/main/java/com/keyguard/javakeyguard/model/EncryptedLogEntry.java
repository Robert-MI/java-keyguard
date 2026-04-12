package com.keyguard.javakeyguard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptedLogEntry {
    private Long id;
    private String timestamp;
    private String encryptedContext;
    private String encryptedPayload;
}