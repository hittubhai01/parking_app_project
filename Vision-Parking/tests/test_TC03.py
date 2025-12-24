from .common import wait_for_element
from .auth_helpers import login, register_user, generate_unique_email, generate_unique_username, generate_unique_phone
from appium.webdriver.common.appiumby import AppiumBy
from tests.constants import REGISTER_PASSWORD, REGISTER_ADDRESS

def test_login_after_registration(driver):
    # 1. Generate unique credentials for the new user
    unique_username = generate_unique_username()
    unique_email = generate_unique_email()
    password = REGISTER_PASSWORD  # Using a constant password is fine for this test
    phone = generate_unique_phone()
    address = REGISTER_ADDRESS

    # 2. Navigate to the registration screen
    wait_for_element(driver, (AppiumBy.ID, 'btnGetStarted')).click()
    wait_for_element(driver, (AppiumBy.ID, 'tvRegister')).click()

    # 3. Register the new user. The helper function will leave us on the login screen.
    register_user(driver, unique_username, unique_email, password, phone, address)

    # 4. Now, log in with the exact same credentials we just created.
    login(driver, unique_email, password)
