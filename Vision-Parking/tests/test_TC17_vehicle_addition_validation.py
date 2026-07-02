"""
Test Case TC17: Vehicle Addition - Form Validation (FIXED v3)
=============================================================
Uses open_drawer_and_navigate() helper for reliable drawer navigation.
"""

import time
from appium.webdriver.common.appiumby import AppiumBy
from tests.common import wait_for_element, assert_element_is_visible, assert_validation_message, is_element_visible, handle_permission_dialog, open_drawer_and_navigate
from tests.auth_helpers import login
from tests.constants import REGISTER_EMAIL, REGISTER_PASSWORD


def scroll_to_element(driver, element_locator, max_scrolls=5):
    for i in range(max_scrolls):
        try:
            element = driver.find_element(*element_locator)
            if element.is_displayed():
                return element
        except:
            pass
        size = driver.get_window_size()
        driver.swipe(size['width'] // 2, int(size['height'] * 0.8),
                     size['width'] // 2, int(size['height'] * 0.2), 500)
        time.sleep(0.5)
    return wait_for_element(driver, element_locator)


def navigate_to_add_vehicle(driver):
    """Helper: login and navigate to the Add Vehicle screen."""
    wait_for_element(driver, (AppiumBy.ID, 'btnGetStarted')).click()
    time.sleep(1)
    login(driver, REGISTER_EMAIL, REGISTER_PASSWORD)
    time.sleep(3)
    handle_permission_dialog(driver, timeout=3)
    time.sleep(2)

    open_drawer_and_navigate(driver, 'nav_vehicles', 'My Vehicles')
    time.sleep(2)

    add_vehicle_button = (AppiumBy.ID, 'fabAddVehicle')
    wait_for_element(driver, add_vehicle_button).click()
    time.sleep(1)


def test_vehicle_addition_empty_fields_validation(driver):
    """Test that validation errors appear when submitting vehicle form with empty fields."""
    navigate_to_add_vehicle(driver)

    submit_button = scroll_to_element(driver, (AppiumBy.ID, 'btnSaveVehicle'))
    submit_button.click()
    time.sleep(1)

    expected_error_messages = [
        "Registration number is required",
        "Please enter registration number",
        "required",
        "cannot be empty",
        "Please fill all required fields",
        "Please enter",
        "invalid",
        "minimum",
    ]
    assert_validation_message(driver, expected_error_messages)
    print("✓ Empty fields validation working correctly")


def test_vehicle_addition_invalid_registration_format(driver):
    """Test validation for invalid (too-short) registration number."""
    navigate_to_add_vehicle(driver)

    registration_field = wait_for_element(driver, (AppiumBy.ID, 'etRegistrationNumber'))
    registration_field.clear()
    registration_field.send_keys("AB")  # Too short

    vehicle_name_field = wait_for_element(driver, (AppiumBy.ID, 'etVehicleName'))
    vehicle_name_field.send_keys("Test Vehicle")

    make_field = wait_for_element(driver, (AppiumBy.ID, 'etMake'))
    make_field.send_keys("Honda")

    model_field = wait_for_element(driver, (AppiumBy.ID, 'etModel'))
    model_field.send_keys("City")

    year_field = wait_for_element(driver, (AppiumBy.ID, 'etYear'))
    year_field.send_keys("2020")

    vehicle_type_field = wait_for_element(driver, (AppiumBy.ID, 'actvVehicleType'))
    vehicle_type_field.click()
    time.sleep(0.5)
    try:
        car_option = wait_for_element(driver, (AppiumBy.XPATH, "//*[@text='car' or @text='Car']"), timeout=3)
        car_option.click()
    except:
        vehicle_type_field.clear()
        vehicle_type_field.send_keys("car")
    time.sleep(0.5)

    color_field = scroll_to_element(driver, (AppiumBy.ID, 'etColor'))
    color_field.clear()
    color_field.send_keys("Blue")

    submit_button = scroll_to_element(driver, (AppiumBy.ID, 'btnSaveVehicle'))
    submit_button.click()
    time.sleep(2)

    expected_error_messages = [
        "Invalid registration number",
        "Registration number must be",
        "4-15 characters",
        "invalid format",
        "required",
        "invalid",
        "minimum",
        "Please enter",
    ]
    assert_validation_message(driver, expected_error_messages)
    print("✓ Invalid registration number format validation working correctly")


def test_vehicle_addition_invalid_year(driver):
    """Test validation for invalid vehicle year (far future year)."""
    navigate_to_add_vehicle(driver)

    registration_field = wait_for_element(driver, (AppiumBy.ID, 'etRegistrationNumber'))
    registration_field.clear()
    registration_field.send_keys(f"DL{int(time.time()) % 100000}")

    vehicle_name_field = wait_for_element(driver, (AppiumBy.ID, 'etVehicleName'))
    vehicle_name_field.send_keys("Test Vehicle")

    make_field = wait_for_element(driver, (AppiumBy.ID, 'etMake'))
    make_field.send_keys("Honda")

    model_field = wait_for_element(driver, (AppiumBy.ID, 'etModel'))
    model_field.send_keys("City")

    year_field = wait_for_element(driver, (AppiumBy.ID, 'etYear'))
    year_field.clear()
    year_field.send_keys("2099")  # Far future — always invalid

    vehicle_type_field = wait_for_element(driver, (AppiumBy.ID, 'actvVehicleType'))
    vehicle_type_field.click()
    time.sleep(0.5)
    try:
        car_option = wait_for_element(driver, (AppiumBy.XPATH, "//*[@text='car' or @text='Car']"), timeout=3)
        car_option.click()
    except:
        vehicle_type_field.clear()
        vehicle_type_field.send_keys("car")
    time.sleep(0.5)

    color_field = scroll_to_element(driver, (AppiumBy.ID, 'etColor'))
    color_field.clear()
    color_field.send_keys("Red")

    submit_button = scroll_to_element(driver, (AppiumBy.ID, 'btnSaveVehicle'))
    submit_button.click()
    time.sleep(2)

    expected_error_messages = [
        "Invalid year",
        "Year must be between",
        "future year",
        "invalid",
        "required",
        "Please enter",
        "minimum",
    ]
    assert_validation_message(driver, expected_error_messages)
    print("✓ Invalid year validation working correctly")
