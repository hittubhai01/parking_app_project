import uuid
import random
from appium.webdriver.common.appiumby import AppiumBy
from .common import wait_for_element, handle_permission_dialog

def generate_unique_username():
    return f"testuser_{uuid.uuid4().hex[:8]}"

def generate_unique_email():
    return f"testuser_{uuid.uuid4().hex[:8]}@example.com"

def generate_unique_phone():
    return f"9{random.randint(100000000, 999999999)}"

import time

def login(driver, email, password, expect_success=True):
    """
    Handles the login process.

    Args:
        driver: The Appium driver instance.
        email: The email to enter.
        password: The password to enter.
        expect_success (bool): If True, will wait for the home screen to appear
                             after login. If False, it will not.
    """
    email_input = wait_for_element(driver, (AppiumBy.ID, 'etEmail'))
    email_input.clear()
    email_input.send_keys(email)

    password_input = wait_for_element(driver, (AppiumBy.ID, 'etPassword'))
    password_input.clear()
    password_input.send_keys(password)

    submit_button = wait_for_element(driver, (AppiumBy.ID, 'btnLogin'))
    submit_button.click()

    # If we expect the login to be successful, wait for the next screen.
    if expect_success:
        # Wait for either topAppBar to appear, or check for permission dialog and dismiss it.
        # We can poll for up to 30 seconds to accommodate slower emulator/CI response times.
        end_time = time.time() + 30
        success = False
        while time.time() < end_time:
            # Check if topAppBar is present/visible
            try:
                driver.implicitly_wait(1.0)
                el = driver.find_element(AppiumBy.ID, 'topAppBar')
                if el.is_displayed():
                    success = True
                    break
            except:
                pass
            
            # If not found, check if a permission dialog is showing and dismiss it
            handle_permission_dialog(driver, timeout=1)
            time.sleep(0.5)
            
        # Restore standard implicit wait (15.0s)
        driver.implicitly_wait(15.0)
        
        if not success:
            # Fall back to standard wait_for_element to raise the proper pytest failure/traceback
            wait_for_element(driver, (AppiumBy.ID, 'topAppBar'), timeout=1)



def register_user(driver, name, email, password, phone, address):
    wait_for_element(driver, (AppiumBy.ID, 'etName')).send_keys(name)
    wait_for_element(driver, (AppiumBy.ID, 'etEmail')).send_keys(email)
    wait_for_element(driver, (AppiumBy.ID, 'etPassword')).send_keys(password)
    wait_for_element(driver, (AppiumBy.ID, 'etPhone')).send_keys(phone)
    wait_for_element(driver, (AppiumBy.ID, 'etAddress')).send_keys(address)

    wait_for_element(driver, (AppiumBy.ID, 'btnRegister')).click()
    handle_permission_dialog(driver)
    wait_for_element(driver, (AppiumBy.ID, 'btnLogin'), timeout=15)
