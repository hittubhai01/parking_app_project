from appium.webdriver.common.appiumby import AppiumBy
from tests.common import wait_for_element, assert_element_is_visible
from tests.auth_helpers import login
from tests.constants import REGISTER_EMAIL, REGISTER_PASSWORD
import time

def test_bottom_nav_bar_navigation(driver):
    """
    Tests that the bottom navigation bar correctly switches between all screens.
    Steps:
    1. Log in to get to the home screen.
    2. Tap the 'Sessions' icon and verify the Sessions screen loads.
    3. Tap the 'Bookings' icon and verify the Bookings screen loads.
    4. Tap the 'Profile' icon and verify the Profile screen loads.
    5. Tap the 'Home' icon and verify the app returns to the Home screen.
    """
    # 1. Log in to get to the home screen
    wait_for_element(driver, (AppiumBy.ID, 'btnGetStarted')).click()
    login(driver, REGISTER_EMAIL, REGISTER_PASSWORD)
    time.sleep(2) # Allow home screen to settle

    # --- 2. Navigate to Sessions and Verify ---
    # As you noted, this is the green circular icon. We'll assume the ID is 'nav_sessions'.
    sessions_nav_locator = (AppiumBy.ID, "nav_sessions")
    wait_for_element(driver, sessions_nav_locator).click()
    time.sleep(1) # Allow for screen transition

    # A sessions screen has a unique summary TextView.
    sessions_title_locator = (AppiumBy.ID, "tvSessionSummary")
    assert_element_is_visible(driver, sessions_title_locator)

    # --- 3. Navigate to Bookings and Verify ---
    bookings_nav_locator = (AppiumBy.ID, "nav_bookings")
    wait_for_element(driver, bookings_nav_locator).click()
    time.sleep(1)

    # Verify the Bookings screen by checking if the add booking FAB is visible.
    bookings_fab_locator = (AppiumBy.ID, "fabAddBooking")
    assert_element_is_visible(driver, bookings_fab_locator)

    # --- 4. Navigate to Profile and Verify ---
    profile_nav_locator = (AppiumBy.ID, "nav_profile")
    wait_for_element(driver, profile_nav_locator).click()
    time.sleep(1)

    # Verify the Profile screen by looking for the user's email TextView.
    profile_email_locator = (AppiumBy.ID, "tvProfileEmail")
    email_elem = wait_for_element(driver, profile_email_locator)
    assert REGISTER_EMAIL in email_elem.text


    # --- 5. Navigate back to Home and Verify ---
    home_nav_locator = (AppiumBy.ID, "nav_home")
    wait_for_element(driver, home_nav_locator).click()
    time.sleep(1)

    # Verify we are back on the Home screen by looking for the map fragment.
    map_locator = (AppiumBy.ID, "mapFragment")
    assert_element_is_visible(driver, map_locator)
