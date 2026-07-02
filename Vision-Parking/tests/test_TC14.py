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
    time.sleep(3)

    handle_permission_dialog(driver, timeout=3)
    time.sleep(2)

    # 2. Open the drawer using the hamburger button
    hamburger = wait_for_element(driver, (AppiumBy.ACCESSIBILITY_ID, "Open navigation drawer"), timeout=10)
    hamburger.click()
    time.sleep(1.5)

    # 3. Verify the drawer is open — look for 'My Vehicles' text using UiSelector
    try:
        my_vehicles = WebDriverWait(driver, 5).until(
            EC.presence_of_element_located((AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("My Vehicles")'))
        )
        assert my_vehicles.is_displayed(), "Navigation drawer did not open — 'My Vehicles' item not visible"
    except Exception as e:
        pytest.fail(f"Navigation drawer did not open successfully: {e}")

    print("✓ Navigation drawer opened successfully")

    # 4. Close the drawer via Android back key
    driver.press_keycode(4)
    time.sleep(1.5)

    # 5. Verify the drawer is closed — 'My Vehicles' text should not be visible or present
    is_closed = False
    try:
        item = driver.find_element(AppiumBy.ANDROID_UIAUTOMATOR, 'new UiSelector().text("My Vehicles")')
        is_closed = not item.is_displayed()
    except Exception:
        # NoSuchElementException means it's not present (closed/destroyed/not visible)
        is_closed = True

    assert is_closed, "The navigation drawer did not close correctly."
    print("✓ Navigation drawer opened and closed correctly")
