package com.keyguard.javakeyguard.keylogger.context;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Service
public class WindowContextProvider {

    public String getActiveApplication() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("mac")) {
            return getMacActiveWindow();
        } else if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return getLinuxActiveWindow();
        } else if (os.contains("win")) {
            return "Windows not yet supported";
        }
        
        return "Unknown OS";
    }

    private String getLinuxActiveWindow() {
        try {
            // կարևորա
            // Requires 'xdotool' to be installed on the Linux system
            String[] cmd = {"xdotool", "getactivewindow", "getwindowname"};
            Process process = Runtime.getRuntime().exec(cmd);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String result = reader.readLine();
                return (result != null && !result.isEmpty()) ? result : "Unknown Linux App";
            }
        } catch (Exception e) {
            return "Linux Error (Is xdotool installed?): " + e.getMessage();
        }
    }

    private String getMacActiveWindow() {
        try {
            String script =
                    "tell application \"System Events\"\n" +
                            "    set frontApp to first application process whose frontmost is true\n" +
                            "    set appName to name of frontApp\n" +
                            "end tell\n" +
                            "if appName contains \"Chrome\" then\n" +
                            "    tell application \"Google Chrome\" to return \"Chrome | \" & title of active tab of front window\n" +
                            "else if appName contains \"Safari\" then\n" +
                            "    tell application \"Safari\" to return \"Safari | \" & name of current tab of front window\n" +
                            "else\n" +
                            "    tell application \"System Events\"\n" +
                            "        tell frontApp\n" +
                            "            try\n" +
                            "                return appName & \" | \" & name of window 1\n" +
                            "            on error\n" +
                            "                return appName\n" +
                            "            end try\n" +
                            "        end tell\n" +
                            "    end tell\n" +
                            "end if";

            String[] cmd = {"/usr/bin/osascript", "-e", script};
            Process process = Runtime.getRuntime().exec(cmd);

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String result = reader.readLine();
                return (result != null && !result.isEmpty()) ? result : "Unknown App";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}