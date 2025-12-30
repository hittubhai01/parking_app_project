#!/bin/bash
set -e

# === Global config ===
export APPIUM_LOG_FILE="/tmp/appium.log"
export TEST_REPORT_FILE="tests/report.html"

cleanup_logcat() {
  echo "Capturing final logcat output..."
  adb logcat -d > /tmp/logcat.txt 2>&1 || touch /tmp/logcat.txt
}
trap cleanup_logcat EXIT

# Ensure we're in Vision-Parking directory
cd "$(dirname "$0")"
echo "Current working directory: $PWD"

echo "Waiting for Android device..."
adb wait-for-device

echo "Polling for emulator boot completion..."
for i in $(seq 1 30); do
  BOOT_STATUS=$(adb shell getprop sys.boot_completed | tr -d '\r')
  if [[ "$BOOT_STATUS" == "1" ]]; then
    echo "✅ Emulator boot completed"
    break
  fi
  echo "⏳ Waiting for emulator to boot ($i/30)..."
  sleep 5
done
if [[ "$BOOT_STATUS" != "1" ]]; then
  echo "❌ Emulator did not boot in time."
  exit 1
fi

echo "Waiting for package manager service..."
for i in $(seq 1 30); do
  if adb shell service check package | grep -q "found"; then
    echo "✅ Package manager service is available"
    break
  fi
  echo "⏳ Waiting for package manager service ($i/30)..."
  sleep 5
done

echo "Sleeping 10 seconds before install..."
sleep 10

echo "Installing app-debug.apk with retries..."
INSTALL_SUCCESS=0
for i in $(seq 1 5); do
  if adb install -r app/build/outputs/apk/debug/app-debug.apk; then
    INSTALL_SUCCESS=1
    echo "✅ APK installed successfully on attempt $i"
    break
  else
    echo "❌ APK install failed on attempt $i, retrying in 30s..."
    sleep 30
  fi
done
if [ $INSTALL_SUCCESS -ne 1 ]; then
  echo "APK install failed after 5 attempts"
  exit 1
fi

echo "Installing Appium globally..."
npm install -g appium
appium driver install uiautomator2

echo "Starting Appium server..."
nohup appium --base-path /wd/hub --log "$APPIUM_LOG_FILE" --log-level debug &
APPIUM_PID=$!

echo "Waiting for Appium to start..."
for i in {1..60}; do
  if nc -z 127.0.0.1 4723; then
    echo "✅ Appium is running"
    break
  fi
  sleep 1
done

if ! nc -z 127.0.0.1 4723; then
  echo "❌ Appium did not start"
  cat "$APPIUM_LOG_FILE"
  exit 1
fi

echo "Running Pytest E2E tests..."
pytest tests \
  -v \
  --maxfail=1 \
  --disable-warnings \
  --html="$TEST_REPORT_FILE" \
  --self-contained-html

PYTEST_EXIT=$?

echo "Stopping Appium..."
kill $APPIUM_PID 2>/dev/null || true
wait $APPIUM_PID 2>/dev/null || true

exit $PYTEST_EXIT