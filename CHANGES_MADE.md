# 🛠️ Codebase Fixes & Setup Guide (Android & Backend Connection)

This guide documents the changes made to resolve compilation failures in Android Studio, correct the "your device is offline" connection issue, and ensure the Android application is fully functional and can connect to the local/remote backend server.

---

## 📋 1. Resolved Compilation & Test Failures

Previously, the Android project could not build or run due to **21 compilation errors** in the unit tests, caused by class signature mismatches and type ambiguity.

### A. Implemented Missing Constructors & Methods in `UserVehicle.java`
* **File:** [UserVehicle.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/java/com/example/visionpark/models/UserVehicle.java)
* **Changes:**
  * Added the missing **4-parameter constructor**:
    ```java
    public UserVehicle(int vehicleId, String registrationNumber, String vehicleName, String vehicleType)
    ```
  * Added the missing **8-parameter constructor**:
    ```java
    public UserVehicle(int vehicleId, String registrationNumber, String vehicleName, String make, String model, Integer year, String vehicleType, String color)
    ```
  * Implemented the **`getDisplayName()`** helper method to correctly format the vehicle details.
  * Implemented the **`getVehicleDetails()`** helper method.
  * Implemented the **`getFormattedVehicleType()`** helper method.
  * Initialized the `isActive` primitive boolean field to default to `true` at the field declaration level to pass default constructor assertions.
  * Appended the `isActive` field inside the `toString()` builder.

### B. Resolved JUnit `assertEquals` Ambiguity in `UserVehicleTest.java`
* **File:** [UserVehicleTest.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/test/java/com/example/visionpark/UserVehicleTest.java)
* **Changes:**
  * Cast primitive numeric comparisons (e.g. `2020` and `2019`) to `Integer.valueOf(...)` to resolve compiler ambiguity between `assertEquals(long, long)` and `assertEquals(Object, Object)`:
    ```java
    assertEquals(Integer.valueOf(2020), testVehicle.getYear());
    assertEquals(Integer.valueOf(2019), vehicle.getYear());
    ```

### C. Fixed Format Duplication in `SlotLocation.java`
* **File:** [SlotLocation.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/java/com/example/visionpark/models/SlotLocation.java)
* **Changes:**
  * Updated `getFormattedLocation()` to check if the `floorName` already contains the word "floor" (e.g., `"Ground Floor"`) before prepending `"Floor "` to it, resolving comparison errors in integration tests.

---

## 🌐 2. Fixed "Device is Offline" & Server Connectivity Issues

The Android OS blocks all cleartext HTTP connections by default, and hardcoded loopback IP addresses (`10.0.2.2`) broke connections when running on physical devices or other networks. Additionally, macOS Control Center/AirPlay blocks port `5000` by default.

### A. Permitted Cleartext (HTTP) Traffic Globally
* **File:** [network_security_config.xml](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/res/xml/network_security_config.xml)
* **Changes:**
  * Replaced strict hardcoded domain restrictions with a global configuration allowing cleartext (HTTP) traffic. This allows local IP configurations (like `192.168.x.x` for physical devices) to communicate without being blocked:
    ```xml
    <?xml version="1.0" encoding="utf-8"?>
    <network-security-config>
        <base-config cleartextTrafficPermitted="true">
            <trust-anchors>
                <certificates src="system" />
                <certificates src="user" />
            </trust-anchors>
        </base-config>
    </network-security-config>
    ```

### B. Resolved Host Port `5000` Conflict (macOS Control Center/AirPlay)
* **Files:**
  * [docker-compose.yml](file:///Users/hiteshyadav/Desktop/PARKING-APP/Backend/docker-compose.yml)
  * [docker-compose.fastapi.yml](file:///Users/hiteshyadav/Desktop/PARKING-APP/Backend/docker-compose.fastapi.yml)
* **Changes:**
  * Changed the host-exposed port mapping of the app container from `"5000:5000"` to `"5001:5000"` to avoid conflicts with macOS Control Center/AirPlay, allowing the Docker containers to start successfully.

### C. Dynamic Base URL Resolution
* **Files:** 
  * [ParkingApiService.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/java/com/example/visionpark/api/ParkingApiService.java)
  * [VehicleApiService.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/java/com/example/visionpark/network/VehicleApiService.java)
* **Changes:**
  * Eliminated all hardcoded `10.0.2.2` API strings.
  * Added dynamic getters that automatically resolve the host, scheme, and ports (`5001` for backend API to match host configuration, `80` for Nginx proxy) based on the single source of truth: `BuildConfig.BASE_URL` (configured in your `build.gradle.kts` file).
  * Now, simply changing the URL in `build.gradle.kts` updates the endpoint for the entire app.

### D. Corrected App Health Check Target Path to Prevent False "Offline" Falls
* **File:** [ParkingApiService.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/java/com/example/visionpark/api/ParkingApiService.java)
* **Changes:**
  * Changed the connectivity test endpoint from the root `/` to `/health`.
  * **Why:** The Flask backend returns a `404` error for the root `/` path. Previously, this caused the app to think the server was unreachable/offline (and fall back to mock data) even though all other API endpoints (like vehicles and sessions) were working perfectly.

---

## ⚡ 3. Resolved Foreground Service Crashes (Android 14+)

When starting a session or checking in/out, the application crashed with a **"VisionPark keeps stopping"** dialog and a `validateOurIsForegroundServiceType` error log.

### A. Declared Missing FGS Permission in Manifest
* **File:** [AndroidManifest.xml](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/AndroidManifest.xml)
* **Changes:**
  * Added the mandatory `FOREGROUND_SERVICE_DATA_SYNC` permission:
    ```xml
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE_DATA_SYNC" />
    ```
  * **Why:** The tracking service `SessionTrackingService` has a foreground service type declared as `"dataSync"`. Starting Android 14 (API level 34+), the system throws a `SecurityException` and crashes the app immediately if the matching permission is not explicitly requested.

### B. Updated Programmatic Foreground Service Startup Type
* **File:** [SessionTrackingService.java](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/src/main/java/com/example/visionpark/services/SessionTrackingService.java)
* **Changes:**
  * Updated the service initialization `startForeground` call to explicitly pass the service type parameter `FOREGROUND_SERVICE_TYPE_DATA_SYNC` on Android 10+ (API 29+):
    ```java
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        startForeground(NOTIFICATION_ID, createNotification(), android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC);
    } else {
        startForeground(NOTIFICATION_ID, createNotification());
    }
    ```

---

## 🚀 4. How to Run the App & Connect to Server

### Step 1: Find your computer's local IP address
* On macOS: Open Terminal and run `ipconfig getifaddr en0` (or check your network settings). Let's say your IP is `192.168.1.15`.

### Step 2: Update Gradle Configuration
1. Open [build.gradle.kts](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/app/build.gradle.kts).
2. For testing on a **Physical Device**:
   * Change `BASE_URL` configuration from `"http://10.0.2.2/"` to `"http://your_computer_ip/"` (e.g. `"http://192.168.1.15/"`).
3. For testing on the **Android Emulator**:
   * Keep it as `"http://10.0.2.2/"` (default).

### Step 3: Run the Backend Services
1. Navigate to the backend directory:
   ```bash
   cd /Users/hiteshyadav/Desktop/PARKING-APP/Backend
   ```
2. Start the FastAPI backend:
   ```bash
   docker compose -f docker-compose.fastapi.yml up --build
   ```
3. Seed the PostgreSQL database:
   ```bash
   docker cp COMPLETE_DATABASE_SETUP_FIXED.sql postgres_db:/setup.sql
   docker exec -it postgres_db psql -U parking_user -d parking_db -f /setup.sql
   ```

### Step 4: Run the Android App
1. Open the [Vision-Parking](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking) folder in **Android Studio**.
2. Make sure your device (emulator or physical device) is connected.
3. Click the **Run** button (green play icon) in Android Studio.
4. The app will sync Gradle, build, install, and successfully communicate with the active backend server!

---

## 🧪 5. Android E2E Instrumented Test Script Setup & Corrections
The Appium-based E2E script [run_e2e.sh](file:///Users/hiteshyadav/Desktop/PARKING-APP/Vision-Parking/run_e2e.sh) has been made fully functional:
1. **Granted Execute Permission:** Set execute bit (`chmod +x run_e2e.sh`) to prevent `zsh: permission denied` errors.
2. **Automated Gradle Compile Checks:** Added check in the script to compile the debug APK via `./gradlew assembleDebug` automatically if the APK file is missing.
3. **Local Appium Execution (No-Sudo):** Changed Appium installation from global (`npm install -g`) to local (`npm install appium --no-save`) and run commands via `npx appium` to avoid OS write permission errors (`EACCES`) on macOS.
