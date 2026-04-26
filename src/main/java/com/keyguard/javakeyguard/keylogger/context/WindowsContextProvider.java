package com.keyguard.javakeyguard.keylogger.context;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef.HWND;
import org.springframework.stereotype.Service;

@Service
public class WindowsContextProvider {

    public String getActiveApplication() {
        try {
            HWND fgWindow = User32.INSTANCE.GetForegroundWindow();
            if (fgWindow == null) {
                return "Unknown App";
            }

            int titleLength = User32.INSTANCE.GetWindowTextLength(fgWindow) + 1;
            char[] title = new char[titleLength];

            User32.INSTANCE.GetWindowText(fgWindow, title, titleLength);
            String windowTitle = new String(title).trim();

            return windowTitle.isEmpty() ? "Unknown App" : windowTitle;

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}