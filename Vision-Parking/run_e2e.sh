#!/bin/bash
set -e

# === Global config ===
if [ -z "$ANDROID_HOME" ]; then
  if [ -d "/Users/hiteshyadav/Library/Android/sdk" ]; then
    export ANDROID_HOME="/Users/hiteshyadav/Library/Android/sdk"
    export ANDROID_SDK_ROOT="/Users/hiteshyadav/Library/Android/sdk"
    export PATH="$PATH:$ANDROID_HOME/emulator:$ANDROID_HOME/platform-tools"
  fi
fi
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
echo "Checking if app-debug.apk exists..."
if [ ! -f "app/build/outputs/apk/debug/app-debug.apk" ]; then
  echo "🛠️ app-debug.apk not found. Compiling debug APK now..."
  ./gradlew assembleDebug
fi

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

echo "Installing Appium locally..."
npm install appium --no-save
npx appium driver install uiautomator2 >/dev/null 2>&1 || true

echo "Starting Appium server..."
nohup npx appium --base-path /wd/hub --log "$APPIUM_LOG_FILE" --log-level debug &
APPIUM_PID=$!

echo "Waiting for Appium to start..."
for i in {1..60}; do
  if curl -s http://127.0.0.1:4723/wd/hub/status | grep -q "ready"; then
    echo "✅ Appium is fully ready and responding"
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
PYTEST_CMD="pytest"
if [ -d ".venv" ]; then
  PYTEST_CMD=".venv/bin/pytest"
fi

$PYTEST_CMD tests \
  -v \
  --disable-warnings \
  --html="$TEST_REPORT_FILE" \
  --self-contained-html \
  --maxfail=3


PYTEST_EXIT=$?

echo "Stopping Appium..."
kill $APPIUM_PID 2>/dev/null || true
wait $APPIUM_PID 2>/dev/null || true

exit $PYTEST_EXIT