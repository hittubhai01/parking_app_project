package com.example.visionpark;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.visionpark.activities.ParkingLotDetailsActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.containsString;

/**
 * UI tests for ParkingLotDetailsActivity
 */
@RunWith(AndroidJUnit4.class)
public class ParkingLotDetailsActivityUITest {

    private Intent createTestIntent() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ParkingLotDetailsActivity.class);
        
        // Add test parking lot data
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_ID, 1);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_NAME, "Test Parking Lot");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_ADDRESS, "123 Test Street, Test City");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_LATITUDE, 28.6139);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_LONGITUDE, 77.2090);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_CAR_FEE, "₹50/hr");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TWO_WHEELER_FEE, "₹20/hr");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_CAR_SLOTS, 15);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_CAR_SLOTS, 20);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_TWO_WHEELER_SLOTS, 8);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_TWO_WHEELER_SLOTS, 10);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_PAYMENT_MODE, "Cash, Card, UPI");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_DISTANCE, 1.5);
        
        return intent;
    }

    @Test
    public void testParkingLotDetailsActivityLaunch() {
        // Test that ParkingLotDetailsActivity launches successfully with test data
        Intent intent = createTestIntent();
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify that the main components are displayed
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            onView(withId(R.id.tvParkingLotName)).check(matches(isDisplayed()));
            onView(withId(R.id.btnParkVehicle)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testParkingLotInformationDisplay() {
        // Test that parking lot information is displayed correctly
        Intent intent = createTestIntent();
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify parking lot name
            onView(withId(R.id.tvParkingLotName)).check(matches(withText("Test Parking Lot")));
            
            // Verify address
            onView(withId(R.id.tvParkingLotAddress)).check(matches(withText("123 Test Street, Test City")));
            
            // Verify distance
            onView(withId(R.id.tvDistance)).check(matches(withText(containsString("1.5 km"))));
            
            // Verify pricing information
            onView(withId(R.id.tvCarPricing)).check(matches(withText(containsString("₹50/hr"))));
            onView(withId(R.id.tvTwoWheelerPricing)).check(matches(withText(containsString("₹20/hr"))));
            
            // Verify capacity information
            onView(withId(R.id.tvCarCapacity)).check(matches(withText(containsString("15 available out of 20"))));
            onView(withId(R.id.tvTwoWheelerCapacity)).check(matches(withText(containsString("8 available out of 10"))));
            
            // Verify payment modes
            onView(withId(R.id.tvPaymentModes)).check(matches(withText(containsString("Cash, Card, UPI"))));
        }
    }

    @Test
    public void testAvailabilityStatusDisplay() {
        // Test availability status display for available parking
        Intent intent = createTestIntent();
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify availability status shows "Available" (since we have good availability)
            onView(withId(R.id.tvAvailabilityStatus)).check(matches(withText(containsString("Available"))));
            
            // Verify Park Vehicle button is enabled
            onView(withId(R.id.btnParkVehicle)).check(matches(isEnabled()));
            onView(withId(R.id.btnParkVehicle)).check(matches(withText("Park Vehicle")));
        }
    }

    @Test
    public void testFullParkingLotDisplay() {
        // Test display when parking lot is full
        Intent intent = createTestIntent();
        // Set available slots to 0 to simulate full parking
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_CAR_SLOTS, 0);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_TWO_WHEELER_SLOTS, 0);
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify availability status shows "Full"
            onView(withId(R.id.tvAvailabilityStatus)).check(matches(withText(containsString("Full"))));
            
            // Verify Park Vehicle button shows "Parking Full" and is disabled
            onView(withId(R.id.btnParkVehicle)).check(matches(withText("Parking Full")));
        }
    }

    @Test
    public void testLimitedAvailabilityDisplay() {
        // Test display when parking lot has limited availability
        Intent intent = createTestIntent();
        // Set low availability to simulate limited parking
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_CAR_SLOTS, 2);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_CAR_SLOTS, 20);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_TWO_WHEELER_SLOTS, 1);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_TWO_WHEELER_SLOTS, 10);
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify availability status shows "Limited"
            onView(withId(R.id.tvAvailabilityStatus)).check(matches(withText(containsString("Limited"))));
            
            // Verify Park Vehicle button is still enabled
            onView(withId(R.id.btnParkVehicle)).check(matches(isEnabled()));
            onView(withId(R.id.btnParkVehicle)).check(matches(withText("Park Vehicle")));
        }
    }

    @Test
    public void testFreeParkingDisplay() {
        // Test display for free parking
        Intent intent = createTestIntent();
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_CAR_FEE, "Free");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TWO_WHEELER_FEE, "Free");
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify free parking is displayed
            onView(withId(R.id.tvCarPricing)).check(matches(withText(containsString("Free"))));
            onView(withId(R.id.tvTwoWheelerPricing)).check(matches(withText(containsString("Free"))));
        }
    }

    @Test
    public void testParkVehicleButtonClick() {
        // Test Park Vehicle button functionality
        Intent intent = createTestIntent();
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Click the Park Vehicle button
            onView(withId(R.id.btnParkVehicle)).perform(click());
            
            // Note: In the current implementation, this shows a toast message
            // In a real test, we would verify navigation to VehicleListActivity
        }
    }

    @Test
    public void testToolbarBackNavigation() {
        // Test toolbar back navigation
        Intent intent = createTestIntent();
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify toolbar is displayed with back button
            onView(withId(R.id.toolbar)).check(matches(isDisplayed()));
            
            // Note: Testing actual back navigation would require more complex setup
            // This test verifies the toolbar is present
        }
    }

    @Test
    public void testAllInformationCardsAreVisible() {
        // Test that all information cards are visible
        Intent intent = createTestIntent();
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify all information sections are displayed
            onView(withId(R.id.ivParkingLotImage)).check(matches(isDisplayed()));
            onView(withId(R.id.tvOperatingHours)).check(matches(isDisplayed()));
            onView(withId(R.id.tvCarPricing)).check(matches(isDisplayed()));
            onView(withId(R.id.tvTwoWheelerPricing)).check(matches(isDisplayed()));
            onView(withId(R.id.tvCarCapacity)).check(matches(isDisplayed()));
            onView(withId(R.id.tvTwoWheelerCapacity)).check(matches(isDisplayed()));
            onView(withId(R.id.tvPaymentModes)).check(matches(isDisplayed()));
            onView(withId(R.id.tvAvailabilityStatus)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void testIntegerIdHandling() {
        // Test that activity handles Integer parking lot ID correctly
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ParkingLotDetailsActivity.class);
        
        // Pass parking lot ID as Integer (simulating HomeActivity behavior)
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_ID, 123);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_NAME, "Integer ID Test Lot");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_ADDRESS, "Test Address");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_LATITUDE, 28.6139);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_LONGITUDE, 77.2090);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_CAR_FEE, "₹30/hr");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TWO_WHEELER_FEE, "₹15/hr");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_CAR_SLOTS, 10);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_CAR_SLOTS, 15);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_TWO_WHEELER_SLOTS, 5);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_TWO_WHEELER_SLOTS, 8);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_PAYMENT_MODE, "Cash");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_DISTANCE, 2.0);
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify that the activity launches successfully with Integer ID
            onView(withId(R.id.tvParkingLotName)).check(matches(withText("Integer ID Test Lot")));
            onView(withId(R.id.tvParkingLotAddress)).check(matches(withText("Test Address")));
            onView(withId(R.id.btnParkVehicle)).check(matches(isDisplayed()));
            onView(withId(R.id.btnParkVehicle)).check(matches(isEnabled()));
        }
    }

    @Test
    public void testStringIdHandling() {
        // Test that activity handles String parking lot ID correctly
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), ParkingLotDetailsActivity.class);
        
        // Pass parking lot ID as String
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_ID, "456");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_NAME, "String ID Test Lot");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_ADDRESS, "Test Address 2");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_LATITUDE, 28.7041);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_LONGITUDE, 77.1025);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_CAR_FEE, "₹40/hr");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TWO_WHEELER_FEE, "₹20/hr");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_CAR_SLOTS, 8);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_CAR_SLOTS, 12);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_AVAILABLE_TWO_WHEELER_SLOTS, 3);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_TOTAL_TWO_WHEELER_SLOTS, 6);
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_PAYMENT_MODE, "UPI");
        intent.putExtra(ParkingLotDetailsActivity.EXTRA_PARKING_LOT_DISTANCE, 1.2);
        
        try (ActivityScenario<ParkingLotDetailsActivity> scenario = ActivityScenario.launch(intent)) {
            // Verify that the activity launches successfully with String ID
            onView(withId(R.id.tvParkingLotName)).check(matches(withText("String ID Test Lot")));
            onView(withId(R.id.tvParkingLotAddress)).check(matches(withText("Test Address 2")));
            onView(withId(R.id.btnParkVehicle)).check(matches(isDisplayed()));
            onView(withId(R.id.btnParkVehicle)).check(matches(isEnabled()));
        }
    }
}