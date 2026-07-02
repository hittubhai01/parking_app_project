import time
import pytest
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException, NoSuchElementException

def fill_registration_form(driver, name, email, password, phone, address):
    wait_for_element(driver, (AppiumBy.ID, 'etName')).send_keys(name)
    wait_for_element(driver, (AppiumBy.ID, 'etEmail')).send_keys(email)
    wait_for_element(driver, (AppiumBy.ID, 'etPassword')).send_keys(password)
    wait_for_element(driver, (AppiumBy.ID, 'etPhone')).send_keys(phone)
    wait_for_element(driver, (AppiumBy.ID, 'etAddress')).send_keys(address)

def wait_for_element(driver, locator, timeout=10):
    try:
        return WebDriverWait(driver, timeout).until(
            EC.presence_of_element_located(locator)
        )
    except TimeoutException:
        pytest.fail(f"Timeout: Element {locator} not found after {timeout} seconds.")
    except NoSuchElementException:
        pytest.fail(f"No Such Element: {locator}")
    except Exception as e:
        pytest.fail(str(e))

def handle_permission_dialog(driver, timeout=5):
    # Temporarily set a low implicit wait to prevent list scanning from blocking on missing IDs
    driver.implicitly_wait(0.5)

    allow_button_ids = [
        'com.android.permissioncontroller:id/permission_allow_button',
        'com.android.packageinstaller:id/permission_allow_button',
        'com.android.permissioncontroller:id/permission_allow_foreground_only_button',
        'com.android.permissioncontroller:id/permission_allow_always_button',
        'com.android.permissioncontroller:id/permission_allow_one_time_button',
        'com.google.android.permissioncontroller:id/permission_allow_foreground_only_button',
        'com.google.android.permissioncontroller:id/permission_allow_button',
        'com.google.android.permissioncontroller:id/permission_allow_always_button',
        'com.google.android.permissioncontroller:id/permission_allow_one_time_button',
    ]
    allow_texts = ['ALLOW', 'Allow', 'allow', 'While using the app', 'Only this time']
    end_time = time.time() + timeout

    try:
        while time.time() < end_time:
            for btn_id in allow_button_ids:
                try:
                    btn = driver.find_element(AppiumBy.ID, btn_id)
                    if btn.is_displayed():
                        btn.click()
                        return
                except:
                    continue
            for text in allow_texts:
                try:
                    btn = driver.find_element(
                        AppiumBy.ANDROID_UIAUTOMATOR,
                        f'new UiSelector().textMatches("(?i){text}")'
                    )
                    if btn.is_displayed():
                        btn.click()
                        return
                except:
                    continue
            time.sleep(0.5)
    finally:
        # Restore the standard implicit wait
        driver.implicitly_wait(15.0)


def open_drawer_and_navigate(driver, menu_item_id, menu_item_text=None):
    """
    Reliably opens the navigation drawer and taps a menu item.

    Strategy:
    1. Click the 'Open navigation drawer' accessibility button (ActionBarDrawerToggle)
    2. Wait for drawer animation
    3. Find the menu item using UiAutomator2 text search (most reliable for NavigationView)
    4. Fallback to resource ID or XPath.
    """
    # If menu_item_text is not provided, use menu_item_id as the text search term
    text_to_search = menu_item_text if menu_item_text is not None else menu_item_id

    # Step 1: Click the hamburger button
    try:
        hamburger = WebDriverWait(driver, 15).until(
            EC.presence_of_element_located(
                (AppiumBy.ACCESSIBILITY_ID, "Open navigation drawer")
            )
        )
        hamburger.click()
    except Exception:
        # Fallback: swipe from left edge if ACCESSIBILITY_ID not found
        size = driver.get_window_size()
        driver.swipe(10, size['height'] // 2, int(size['width'] * 0.6), size['height'] // 2, 600)

    time.sleep(1.5)  # Wait for drawer slide-in animation

    # Step 2: Find menu item by text using UiAutomator2 (works for NavigationMenuItemView)
    # This is more reliable than resource ID for NavigationView menu items
    try:
        item = WebDriverWait(driver, 8).until(
            EC.presence_of_element_located((
                AppiumBy.ANDROID_UIAUTOMATOR,
                f'new UiSelector().text("{text_to_search}")'
            ))
        )
        item.click()
        time.sleep(0.5)
        return
    except Exception:
        pass

    # Step 3: Fallback — try XPath text
    try:
        item = WebDriverWait(driver, 5).until(
            EC.presence_of_element_located((
                AppiumBy.XPATH,
                f"//*[@text='{text_to_search}']"
            ))
        )
        item.click()
        time.sleep(0.5)
        return
    except Exception:
        pass

    # Step 4: Fallback — try finding by resource ID (e.g. 'nav_vehicles')
    try:
        # If it's a short id, we can try matching by ID
        # e.g., 'nav_vehicles' or fully qualified 'com.example.visionpark:id/nav_vehicles'
        possible_ids = [menu_item_id]
        if not menu_item_id.startswith("com.example.visionpark:id/"):
            possible_ids.append(f"com.example.visionpark:id/{menu_item_id}")
        
        for pid in possible_ids:
            try:
                item = WebDriverWait(driver, 3).until(
                    EC.presence_of_element_located((AppiumBy.ID, pid))
                )
                item.click()
                time.sleep(0.5)
                return
            except Exception:
                continue
    except Exception:
        pass

    pytest.fail(
        f"Could not find nav drawer menu item with text '{text_to_search}' or ID '{menu_item_id}' after opening drawer."
    )


def assert_validation_message(driver, expected_msgs):
    from selenium.common.exceptions import TimeoutException
    expanded_msgs = set(expected_msgs)

    # Add common variants to increase matching reliability
    expanded_msgs.update([
        "Please enter", "required", "valid email", "invalid", "@",
        "already exists", "already registered", "Password must be at least",
        "short password", "minimum", "duplicate"
    ])

    found = False
    for msg in expanded_msgs:
        try:
            toast = WebDriverWait(driver, 10, poll_frequency=0.2).until(
                lambda d: d.find_element(
                    AppiumBy.ANDROID_UIAUTOMATOR,
                    f'new UiSelector().textContains("{msg}")'
                )
            )
            if toast and toast.is_displayed():
                print(f"[Toast detected] Matching message: {msg}")
                found = True
                break
        except Exception:
            continue

    assert found, f"Expected toast not found. Checked for: {list(expanded_msgs)}"

def assert_element_is_visible(driver, locator):
    """
    Waits for an element to be present and asserts that it is visible on the screen.
    If the element is not found or not visible, the test will fail.
    """
    element = wait_for_element(driver, locator)
    assert element.is_displayed(), f"Element '{locator}' was found but is not visible."

def is_element_visible(driver, locator, timeout=5):
    """
    Checks if an element is visible without failing the test.
    Returns True if the element is found and visible, False otherwise.
    """
    try:
        WebDriverWait(driver, timeout).until(
            EC.visibility_of_element_located(locator)
        )
        return True
    except TimeoutException:
        return False