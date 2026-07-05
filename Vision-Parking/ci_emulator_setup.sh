#!/usr/bin/env bash
# ci_emulator_setup.sh
# Sets up the emulator environment, installs the APK, starts Appium,
# runs the Pytest E2E suite, then tears everything down.
set -euo pipefail

APK_PATH="Vision-Parking/app/build/outputs/apk/debug/app-debug.apk"
APP_PACKAGE="com.example.visionpark"
APPIUM_LOG="/tmp/appium.log"
REPORT_DIR="Vision-Parking/tests"

echo "=== CI Emulator Setup ==="

# ── 1. Wait for boot ──────────────────────────────────────────────────────────
echo "Waiting for emulator to boot..."
adb wait-for-device
for i in $(seq 1 60); do
  BOOT=$(adb shell getprop sys.boot_completed 2>/dev/null | tr -d '\r' || echo "0")
  if [ "$BOOT" = "1" ]; then
    echo "Emulator booted after ${i}s"
    break
  fi
  sleep 5
done

# ── 2. Unlock screen ──────────────────────────────────────────────────────────
adb shell input keyevent 82 || true
adb shell wm dismiss-keyguard 2>/dev/null || true
adb shell cmd statusbar collapse 2>/dev/null || true

# ── 3. Install APK ───────────────────────────────────────────────────────────
echo "Installing APK..."
adb install -r -t "$APK_PATH"
echo "APK installed: $APP_PACKAGE"

# ── 4. Start Appium ───────────────────────────────────────────────────────────
echo "Starting Appium server..."
npm install -g appium@2 --silent
appium driver install uiautomator2 --source npm
appium --log "$APPIUM_LOG" --port 4723 &
APPIUM_PID=$!
echo "Appium PID: $APPIUM_PID"

for i in $(seq 1 30); do
  if curl -sf http://127.0.0.1:4723/status > /dev/null 2>&1; then
    echo "Appium ready after ${i}s"
    break
  fi
  sleep 3
done

# ── 5. Run E2E tests ──────────────────────────────────────────────────────────
echo "Running E2E test suite..."
cd Vision-Parking
python -m pytest tests/ \
  -v --tb=short \
  --html=tests/report.html \
  --self-contained-html \
  || EXIT_CODE=$?

# ── 6. Tear down ──────────────────────────────────────────────────────────────
echo "Stopping Appium..."
kill "$APPIUM_PID" 2>/dev/null || true

exit "${EXIT_CODE:-0}"
