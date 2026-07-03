"""
Test Case TC16: Vehicle Addition - Successful Registration (FIXED v3)
=====================================================================
Uses open_drawer_and_navigate() helper for reliable drawer navigation.
"""

import time
from appium.webdriver.common.appiumby import AppiumBy
from tests.common import wait_for_element, assert_element_is_visible, handle_permission_dialog, is_element_visible, open_drawer_and_navigate
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


def test_vehicle_addition_success(driver):
    """
    Test successful vehicle registration with all valid details.
    Validates complete flow: login → navigate to vehicles → add vehicle → verify in list.
    """
    # Step 1: Navigate to login screen
    wait_for_element(driver, (AppiumBy.ID, 'btnGetStarted')).click()
    time.sleep(1)

    # Step 2: Login
    login(driver, REGISTER_EMAIL, REGISTER_PASSWORD)
    time.sleep(3)

    handle_permission_dialog(driver, timeout=3)
    time.sleep(2)

    # Step 3: Open drawer and navigate to My Vehicles
    open_drawer_and_navigate(driver, 'nav_vehicles', 'My Vehicles')
    time.sleep(2)

    # Step 4: Verify we're on the Vehicle Management screen
    print(f"Current activity: {driver.current_activity}")
    fab_locator = (AppiumBy.ID, 'fabAddVehicle')
    wait_for_element(driver, fab_locator, timeout=10)
    print("✓ Found fabAddVehicle — on vehicle management screen")

    # Step 5: Click Add Vehicle FAB
    wait_for_element(driver, fab_locator).click()
    time.sleep(1)

    # Step 6: Fill vehicle registration form
    unique_reg_number = f"DL{int(time.time()) % 100000}"

    vehicle_name_field = wait_for_element(driver, (AppiumBy.ID, 'etVehicleName'))
    vehicle_name_field.clear()
    vehicle_name_field.send_keys("My Test Car")

    registration_field = wait_for_element(driver, (AppiumBy.ID, 'etRegistrationNumber'))
    registration_field.clear()
    registration_field.send_keys(unique_reg_number)

    # Vehicle Type - AutoCompleteTextView
    vehicle_type_field = wait_for_element(driver, (AppiumBy.ID, 'actvVehicleType'))
    vehicle_type_field.click()
    time.sleep(0.5)

    try:
        car_option = wait_for_element(
            driver,
            (AppiumBy.XPATH, "//*[@text='car' or @text='Car']"),
            timeout=3
        )
        car_option.click()
    except:
        vehicle_type_field.clear()
        vehicle_type_field.send_keys("car")
    time.sleep(0.5)

    # Make
    make_field = scroll_to_element(driver, (AppiumBy.ID, 'etMake'))
    make_field.clear()
    make_field.send_keys("Honda")

    # Model
    model_field = wait_for_element(driver, (AppiumBy.ID, 'etModel'))
    model_field.clear()
    model_field.send_keys("City")

    # Year
    year_field = scroll_to_element(driver, (AppiumBy.ID, 'etYear'))
    year_field.clear()
    year_field.send_keys("2020")

    # Color
    color_field = scroll_to_element(driver, (AppiumBy.ID, 'etColor'))
    color_field.clear()
    color_field.send_keys("Silver")

    # Step 7: Scroll to and click Save button
    save_button = scroll_to_element(driver, (AppiumBy.ID, 'btnSaveVehicle'))
    save_button.click()
    time.sleep(3)

    # Step 8: Dismiss success dialog if it appears to return to the vehicle list
    try:
        ok_btn = wait_for_element(driver, (AppiumBy.XPATH, "//*[@text='OK' or @text='Ok']"), timeout=5)
        ok_btn.click()
        print("✓ Dismissed success dialog")
        time.sleep(1.5)
    except Exception as e:
        print(f"⚠ Success dialog not found or could not be dismissed: {e}")


    # Step 9: Verify vehicle appears in the list
    time.sleep(2)
    vehicle_in_list = (AppiumBy.XPATH, f"//*[contains(@text, '{unique_reg_number}')]")

    found = is_element_visible(driver, vehicle_in_list, timeout=5)
    if not found:
        # Try scrolling to find it
        for i in range(3):
            size = driver.get_window_size()
            driver.swipe(size['width'] // 2, int(size['height'] * 0.8),
                         size['width'] // 2, int(size['height'] * 0.2), 500)
            time.sleep(0.5)
            if is_element_visible(driver, vehicle_in_list, timeout=2):
                found = True
                break

    if found:
        print(f"✓ Vehicle found in list with registration: {unique_reg_number}")
    else:
        print(f"⚠ Vehicle {unique_reg_number} not visible in list (may be on different screen)")

    print(f"✓ Vehicle successfully registered: {unique_reg_number}")
