import os
import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

@pytest.fixture(scope="function")
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
    # Prevent 'cmd: Can't find service: settings' error on CI runners where
    # the emulator's settings service is not yet available at session start
    options.set_capability('ignoreHiddenApiPolicyError', True)
    # Skip unlock screen interactions which can also fail on headless CI
    options.set_capability('skipUnlock', True)
    options.no_reset = False

    driver = webdriver.Remote('http://127.0.0.1:4723/wd/hub', options=options)
    yield driver
    driver.quit()
