# Java KeyGuard Agent

A cross-platform (macOS, Windows, Linux) keystroke monitoring agent built in Java using JNativeHook. This research artifact is designed to measure the effectiveness of OS-level input protections.

## 1. Branch Structure
* **`main`**: Contains the stable implementation for **macOS** and **Windows** environments.
* **`linux` / `linux-variant`**: Dedicated branches for **Linux** (X11/evdev) compatibility.

## 2. Prerequisites
* **Java JDK 17 or higher**: Required to compile and run the agent.
* **OS Permissions**:
    * [cite_start]**macOS**: Requires "Input Monitoring" permission in System Settings[cite: 59, 81].
    * **Windows**: Should be run as a standard user for browser testing, or Administrator to hook high-integrity processes.

## 3. Configuration (`application.properties`)
Before execution, the following properties must be configured. These values act as the "identity" of the agent and must match the entries in your Backend database:

* `app.runtime.minutes`: Duration the agent remains active before auto-terminating.
* `agent.id`: Unique UUID for this agent instance.
* `api.hmac.secret`: Secret key used for HMAC-SHA256 request signing.
* `encryption.secret`: 256-bit key used for AES-256 payload encryption.

## 4. Local Testing vs. Production
To switch the API endpoint between local development and the live server:
1. Open `src/main/java/com/keyguard/agent/service/LogApiService.java`.
2. Locate the `UPLOAD_URL` definitions (lines 53-54):
    * **For Production**: Use line 53 (`https://java-keyguard-api.onrender.com/logs/upload`).
    * **For Local Testing**: Comment out line 53 and **uncomment line 54** (`http://localhost:8080/logs/upload`).

## 5. Execution
Build and run using Maven:
```bash
mvn clean package
java -jar target/keyguard-agent-1.0-SNAPSHOT.jar