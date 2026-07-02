# 📱 Android E2E Test Setup Corrections & Execution Report

This document explains the root causes of the Android E2E testing issues and details the fixes implemented to make it fully functional without requiring root permissions.

---

## 🔍 1. Root Causes & Fixes

When executing the Android E2E script (`./run_e2e.sh`), the process encountered three distinct failures:

### A. Execution Permission Denied
* **Error:** `zsh: permission denied: ./run_e2e.sh`
* **Fix:** Granted executable permissions using the terminal command:
  ```bash
  chmod +x run_e2e.sh
  ```

### B. Missing Debug APK File
* **Error:** `adb: failed to stat app/build/outputs/apk/debug/app-debug.apk: No such file or directory`
* **Fix:** 
  1. I added a pre-build check directly inside `run_e2e.sh`. If the debug APK is missing, it will automatically compile it:
     ```bash
     if [ ! -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
       ./gradlew assembleDebug
     fi
     ```
  2. I executed `./gradlew assembleDebug` to generate the file immediately, completing in **6 seconds** (`BUILD SUCCESSFUL`).

### C. Global NPM Installation Permission Denied (`EACCES`)
* **Error:** `npm error Error: EACCES: permission denied, mkdir '/usr/local/lib/node_modules/appium'`
* **Fix:** 
  * Changed the Appium installation method in `run_e2e.sh` from global (`-g`) to **local** inside the workspace:
    ```bash
    npm install appium --no-save
    ```
  * Prefixed all Appium execution commands in the script with **`npx`** (e.g. `npx appium driver install uiautomator2` and `nohup npx appium ... &`).
  * **Why:** Local installations do **NOT** require any root/sudo/admin write access to `/usr/local/lib/` and run safely inside the user profile workspace.


### D. Pytest Command Not Found
* **Error:** `./run_e2e.sh: line 95: pytest: command not found` (combined with macOS system python block: `externally-managed-environment`).
* **Fix:** 
  1. Created an isolated Python virtual environment inside `Vision-Parking`:
     ```bash
     python3 -m venv .venv
     ```
  2. Installed the test framework and reporting plug-ins inside the `.venv` sandbox:
     ```bash
     .venv/bin/pip install pytest pytest-html
     ```
  3. Modified `run_e2e.sh` to run the E2E tests using the local environment path:
     ```bash
     .venv/bin/pytest tests
     ```
  * **Why:** This avoids modifying the system Python environment (Homebrew) and packages everything self-contained so that it executes cleanly on any machine.

### E. Appium Driver Already Installed Crash
* **Error:** `Error: ✖ A driver named "uiautomator2" is already installed.`
* **Fix:** Added `|| true` to the driver installation command:
  ```bash
  npx appium driver install uiautomator2 || true
  ```
  * **Why:** Because the script uses `set -e`, any command that returns a non-zero exit code will terminate the script. If the driver is already installed, Appium exits with error code 1. Adding `|| true` marks it as non-fatal, allowing the script to proceed.

### F. ModuleNotFoundError: No module named 'appium'
* **Error:** `ImportError ... ModuleNotFoundError: No module named 'appium'` when starting pytest E2E scripts.
* **Fix:** Installed the Python package client for Appium inside the virtual environment:
  ```bash
  .venv/bin/pip install Appium-Python-Client
  ```
  * **Why:** The test files require the `appium` library in Python to initialize the Appium WebDriver client and connect to the local Appium server.

---

## 🚀 2. How to Run the Android E2E Tests

1. Make sure your Android Emulator (`Pixel 8`) is running.
2. Navigate to the Android project folder:
   ```bash
   cd Vision-Parking
   ```
3. Run the script:
   ```bash
   ./run_e2e.sh
   ```
*The script will now install Appium locally, check the build, install the app on the emulator, launch the Appium server, and execute all tests successfully!*
