package com.keyguard.javakeyguard.service;

import org.springframework.stereotype.Service;

@Service
public class LogService {

    public String getStatus() {
        return "Log service ready";
    }
}