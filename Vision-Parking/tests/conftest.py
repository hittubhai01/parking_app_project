import os
import pytest
import time
from appium import webdriver
from appium.options.android import UiAutomator2Options

@pytest.fixture(scope="session")
def driver():
    # Collapse statusbar/notification shade to prevent covering the UI
    try:
        os.system("adb shell cmd statusbar collapse")
    except Exception:
        pass

    options = UiAutomator2Options()
    options.platform_name = 'Android'
    options.automation_name = 'UiAutomator2'
    options.device_name = 'Android Emulator'
    options.app_package = 'com.example.visionpark'
    options.app_activity = 'com.example.visionpark.activities.SplashScreenActivity'
    options.implicit_wait_timeout = 30000
    
    # Extended timeouts for CI environment
    options.set_capability('uiautomator2ServerInstallTimeout', 180000)  # 3 minutes
    options.set_capability('uiautomator2ServerLaunchTimeout', 180000)   # 3 minutes
    options.set_capability('adbExecTimeout', 120000)                    # 2 minutes
    options.set_capability('androidInstallTimeout', 120000)             # 2 minutes
    options.set_capability('newCommandTimeout', 600)                    # 10 minutes
    options.set_capability('autoGrantPermissions', True)
    options.set_capability('skipServerInstallation', False)
    options.set_capability('skipDeviceInitialization', False)
    options.set_capability('disableWindowAnimation', True)
    options.set_capability('skipLogcatCapture', True)
    options.set_capability('ignoreHiddenApiPolicyError', True)
    options.set_capability('skipUnlock', True)
    options.no_reset = False

    driver = webdriver.Remote('http://127.0.0.1:4723/wd/hub', options=options)
    yield driver
    driver.quit()

@pytest.fixture(scope="function", autouse=True)
def reset_app_state(driver):
    # Collapse statusbar/notification shade before test starts
    try:
        os.system("adb shell cmd statusbar collapse")
    except Exception:
        pass

    # Stop the app to make sure it is not running
    try:
        driver.terminate_app('com.example.visionpark')
    except Exception:
        pass

    # Clear app data natively (speeds up reset and prevents ADB hangs)
    try:
        driver.execute_script('mobile: clearApp', {'appId': 'com.example.visionpark'})
        time.sleep(1.5)
    except Exception:
        # Fallback to adb shell pm clear if native script is unsupported
        try:
            os.system("adb shell pm clear com.example.visionpark")
            time.sleep(2.0)
        except Exception:
            pass

    # Activate the app fresh on the splash screen
    try:
        driver.activate_app('com.example.visionpark')
    except Exception:
        pass
    
    # Give a tiny sleep to let the splash screen load
    time.sleep(2)

