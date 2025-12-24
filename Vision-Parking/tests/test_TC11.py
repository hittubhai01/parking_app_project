from appium.webdriver.common.appiumby import AppiumBy
from tests.common import (
    wait_for_element,
    assert_element_is_visible,
    is_element_visible  # <-- Import our new helper
)
from tests.auth_helpers import register_user, login
from tests.constants import (
    REGISTER_NAME,
    REGISTER_EMAIL,
    REGISTER_PASSWORD,
    REGISTER_PHONE,
    REGISTER_ADDRESS
)

def test_setup_user_and_verify_map_loads(driver):
    """
    This is a foundational test case. It ensures the primary test user exists
    and then verifies that the home screen map loads correctly after login.
    This test uses a "login-first" strategy to be robust and repeatable.
    """
    # 1. Navigate to login screen and try to log in with the known user
    wait_for_element(driver, (AppiumBy.ID, 'btnGetStarted')).click()
    login(driver, REGISTER_EMAIL, REGISTER_PASSWORD, expect_success=False)

    # 2. Check if login was successful by looking for the map
    map_locator = (AppiumBy.ID, "mapFragment")
    if is_element_visible(driver, map_locator):
        # If map is visible, the user already existed and login was successful.
        # The test's goal is met.
        return

    # 3. If we're here, login failed (user likely doesn't exist).
    # We should still be on the login screen. Now, we register the user.
    wait_for_element(driver, (AppiumBy.ID, 'tvRegister')).click()

    # This registration should be successful
    register_user(
        driver,
        REGISTER_NAME,
        REGISTER_EMAIL,
        REGISTER_PASSWORD,
        REGISTER_PHONE,
        REGISTER_ADDRESS
    )

    # After registration, the helper leaves us on the login screen.
    # Now we log in again. This time it MUST succeed.
    login(driver, REGISTER_EMAIL, REGISTER_PASSWORD, expect_success=True)

    # 4. Verify that the map is visible. This assertion must pass now.
    assert_element_is_visible(driver, map_locator)