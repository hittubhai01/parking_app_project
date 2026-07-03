from appium.webdriver.common.appiumby import AppiumBy
from tests.common import wait_for_element, assert_element_is_visible, is_element_visible, handle_permission_dialog
from tests.auth_helpers import login
from tests.constants import REGISTER_EMAIL, REGISTER_PASSWORD
import time
import pytest
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

def test_burger_menu_opens_and_closes(driver):
    """
    Tests that the side navigation drawer (burger menu) opens and closes correctly.
    Steps:
    1. Log in to get to the home screen.
    2. Click the hamburger button (Open navigation drawer) to open it.
    3. Verify the drawer is open by checking that the 'My Vehicles' menu item is visible.
    4. Close the drawer using the Android back keycode.
    5. Verify the drawer is closed.
    """
    # 1. Log in
    wait_for_element(driver, (AppiumBy.ID, 'btnGetStarted')).click()
    login(driver, REGISTER_EMAIL, REGISTER_PASSWORD)

    # Wait for map fragment to settle the Home screen UI
    map_locator = (AppiumBy.ID, "mapFragment")
    assert_element_is_visible(driver, map_locator)
    time.sleep(2)

    # 2. Open the drawer using the hamburger button
    hamburger = wait_for_element(driver, (AppiumBy.ACCESSIBILITY_ID, "Open navigation drawer"), timeout=10)
    hamburger.click()
    time.sleep(1.5)

    # 3. Verify the drawer is open by looking for the 'My Vehicles' item
    drawer_opened = False
    for locator in [
        (AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("My Vehicles")'),
        (AppiumBy.XPATH, "//*[@text='My Vehicles']"),
        (AppiumBy.ID, "com.example.visionpark:id/nav_vehicles"),
        (AppiumBy.ID, "nav_vehicles")
    ]:
        try:
            elem = WebDriverWait(driver, 5).until(EC.presence_of_element_located(locator))
            if elem.is_displayed():
                drawer_opened = True
                break
        except Exception:
            continue

    assert drawer_opened, "Navigation drawer did not open — 'My Vehicles' item not visible"
    print("✓ Navigation drawer opened successfully")

    # 4. Close the drawer via Android back key
    driver.press_keycode(4)
    time.sleep(1.5)

    # 5. Verify the drawer is closed — 'My Vehicles' should not be visible or present
    is_closed = True
    for locator in [
        (AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("My Vehicles")'),
        (AppiumBy.XPATH, "//*[@text='My Vehicles']"),
        (AppiumBy.ID, "com.example.visionpark:id/nav_vehicles"),
        (AppiumBy.ID, "nav_vehicles")
    ]:
        try:
            item = driver.find_element(*locator)
            if item.is_displayed():
                is_closed = False
                break
        except Exception:
            pass

    assert is_closed, "The navigation drawer did not close correctly."
    print("✓ Navigation drawer opened and closed correctly")

