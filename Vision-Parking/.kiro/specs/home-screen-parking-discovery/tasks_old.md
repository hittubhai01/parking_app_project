# Implementation Plan

- [x] 7. Implement Home Screen Basic UI
  - Create HomeActivity or Fragment for the main screen
  - Design XML layout with SearchView/EditText for search bar
  - Add FrameLayout container for Google Map (initially empty/placeholder)
  - Implement CoordinatorLayout/LinearLayout structure for main content
  - Add container for BottomNavigationView
  - _Requirements: 1.1, 1.2, 1.3, 1.4, 1.5_
  - _Test Strategy: Verify Home Screen layout displays correctly with all major components (search bar, map area, bottom nav area) in place. Test on different screen sizes to ensure responsive layout._

- [x] 8. Implement Bottom Navigation Bar
  - [x] 8.1 Create bottom navigation menu resource file
    - Create res/menu/bottom_nav_menu.xml with Home, My Bookings, Profile items
    - Add appropriate icons for each navigation item
    - _Requirements: 2.2, 2.3_
  - [x] 8.2 Integrate BottomNavigationView with Home Screen
    - Add BottomNavigationView to Home Screen layout
    - Attach menu resource to BottomNavigationView
    - Implement OnItemSelectedListener for item selection handling
    - Add initial logging or Toast feedback for navigation clicks
    - _Requirements: 2.1, 2.4, 2.5_
  - _Test Strategy: Verify Bottom Navigation Bar is visible and contains correct items. Ensure clicking items provides visual feedback and logs/shows Toast messages._

- [ ] 9. Implement Parking Lot options UI
  - [ ] 9.1 Create parking lot list layout and adapter structure
    - Create Fragment or Activity for Parking Lot List display
    - Implement RecyclerView component for list display
    - Create ParkingLotAdapter extending RecyclerView.Adapter
    - Implement ViewHolder pattern with proper data binding
    - _Requirements: 3.1, 3.2_
  - [ ] 9.2 Design parking lot options item layout
    - Design custom XML layout (list_item_parking_lot.xml) for list items
    - Add TextViews for lot name, address, and distance information
    - Add TextView/ImageView for availability badge with background colors
    - Apply proper styling and spacing
    - _Requirements: 3.3, 3.4, 3.5, 3.6_
  - [ ] 9.3 Implement mock data display
    - Create mock ParkingLot data objects
    - Integrate mock data with adapter for testing
    - Ensure proper data binding and display
    - _Requirements: 3.7_
  - _Test Strategy: Verify list layout renders correctly. Ensure mock data is displayed in list items as expected. Test scrolling and item interactions._

- [ ] 10. Implement Parking Lot Details UI
  - Create Activity or Fragment for Parking Lot Details screen
  - Design XML layout with TextViews for lot information display
  - Add sections for availability, rates, operating hours, reviews summary
  - Include "Book Slot" button with proper styling
  - Implement placeholder/mock data display initially
  - Apply Material Components styling consistently
  - _Requirements: 4.1, 4.2, 4.3, 4.4, 4.5_
  - _Test Strategy: Verify Details screen layout renders correctly and displays static/placeholder information. Test "Book Slot" button interaction._

- [ ] 11. Implement Booking Screen UI
  - [ ] 11.1 Create booking screen layout and basic components
    - Create Activity or Fragment for booking functionality
    - Design layout with lot information display section
    - Include "Confirm Booking" button with proper styling
    - _Requirements: 4.2, 4.3_
  - [ ] 11.2 Implement date and time selection
    - Add interactive date picker using DatePickerDialog
    - Add interactive time picker using TimePickerDialog
    - Implement duration calculation logic between start/end times
    - Add estimated cost calculation display with mock rates
    - _Requirements: 4.4, 4.5_
  - _Test Strategy: Verify Booking screen layout is correct. Test opening Date and Time pickers. Ensure duration and cost fields update with mock logic._

- [ ] 12. Implement Booking Confirmation UI
  - Create Activity or Fragment for booking confirmation
  - Design layout with booking details display (ID, date, time, slot info)
  - Add placeholder ImageView for QR code display
  - Include "Go to My Bookings" navigation button
  - Implement proper data display from booking response
  - _Requirements: 4.4, 4.5_
  - _Test Strategy: Verify Confirmation screen layout is correct and displays placeholder booking details._

- [ ] 13. Implement My Bookings Screen UI
  - [ ] 13.1 Create tabbed layout structure
    - Create Activity or Fragment for booking management
    - Implement TabLayout with ViewPager2 for Upcoming/Past tabs
    - Create separate Fragments for each tab content
    - _Requirements: 5.1, 5.2_
  - [ ] 13.2 Implement booking list display
    - Add RecyclerView in each tab Fragment for booking lists
    - Design custom booking list item layout showing date/time, location, status
    - Create BookingAdapter with proper ViewHolder implementation
    - Add mock data display for initial testing
    - _Requirements: 5.3, 5.4, 5.5_
  - _Test Strategy: Verify My Bookings screen displays tabs correctly. Ensure list view within tabs renders mock booking data._

- [ ] 14. Implement Track & Navigate Integration (Google Maps Intent)
  - Create utility method for Google Maps Intent construction
  - Implement ACTION_VIEW Intent with geo: URI format
  - Add parking lot coordinates/address to Intent data
  - Handle cases where Google Maps app is not installed
  - Integrate navigation trigger from My Bookings or Details screens
  - _Requirements: 5.4_
  - _Test Strategy: Test clicking a 'Navigate' or 'Track' button successfully launches Google Maps app with correct destination. Test fallback behavior when Google Maps is not installed._

- [ ] 15. Implement Payment Screen Placeholder UI
  - Create Activity or Fragment for payment functionality
  - Design simple layout with "Payment Options" title
  - Add placeholder text indicating future payment integration
  - Include placeholder buttons for card/UPI payment methods
  - Apply consistent Material Design styling
  - _Requirements: 6.1, 6.2_
  - _Test Strategy: Verify Payment screen placeholder UI displays correctly with proper layout and styling._

- [ ] 16. Implement Rate & Review Screen UI
  - Create Activity or Fragment for rating and review functionality
  - Add RatingBar component for star rating input
  - Include EditText for optional text review input
  - Add Button for optional image upload (initially non-functional)
  - Include "Submit" button for review submission
  - Apply proper layout and styling
  - _Requirements: 8.1_
  - _Test Strategy: Verify Rate & Review screen layout is correct and interactive elements are present. Test RatingBar functionality and text input._

- [ ] 17. Implement Profile / Settings Screen UI
  - Create Activity or Fragment for user profile display
  - Design layout with TextViews for user information (Name, Email, Phone, Address)
  - Add "Edit Profile" button or interactive elements
  - Include "Logout" button with proper styling
  - Use placeholder data initially for testing
  - _Requirements: 6.3, 6.4, 6.5_
  - _Test Strategy: Verify Profile screen layout is correct and displays placeholder user information and action buttons._

- [ ] 18. Implement Navigation: Home/List -> Lot Details
  - Add OnClickListener to parking lot list items in RecyclerView
  - Implement Intent creation for navigation to Parking Lot Details
  - Pass selected lot ID or data via Intent extras
  - Handle navigation from map pins (preparation for future map integration)
  - Test navigation flow with mock data
  - _Requirements: 2.5, 4.1_
  - _Test Strategy: From Home/List view, click on a list item and verify navigation to Details screen occurs. Verify data is passed correctly via Intent extras._

- [ ] 19. Implement Booking Flow Navigation
  - Add OnClickListener to "Book Slot" button in Details screen
  - Implement navigation from Details to Booking screen with data passing
  - Add OnClickListener to "Confirm Booking" button in Booking screen
  - Implement navigation from Booking to Confirmation screen
  - Add OnClickListener to "Go to My Bookings" in Confirmation screen
  - Ensure proper data passing between all booking flow screens
  - _Requirements: 4.2, 4.3, 4.4, 4.5_
  - _Test Strategy: Follow the booking flow path (Details -> Booking -> Confirmation -> My Bookings) and verify correct screen transitions and data passing using mock data._

- [ ] 20. Implement Navigation from My Bookings
  - Add OnClickListener to booking list items in My Bookings
  - Implement navigation to booking details view (reuse or create new screen)
  - Add "Track & Navigate" button/action in booking details
  - Integrate Track & Navigate functionality from Task 14
  - Test complete navigation flow from bookings to navigation
  - _Requirements: 5.3, 5.4_
  - _Test Strategy: From My Bookings, click a booking item and verify navigation to details. From details, click 'Track' and verify Google Maps opens with correct destination._

- [ ] 21. Implement Navigation: Bottom Nav -> Profile
  - Handle Profile item selection in BottomNavigationView OnItemSelectedListener
  - Implement navigation to Profile/Settings screen using Intent
  - Ensure proper activity transitions and back navigation
  - Test navigation from Home to Profile screen
  - _Requirements: 2.4, 2.5, 6.3_
  - _Test Strategy: Click the Profile item in Bottom Navigation and verify the Profile screen is displayed. Test back navigation functionality._

- [ ] 22. Implement Logout Functionality
  - Add OnClickListener to Logout button in Profile screen
  - Integrate with TokenManager utility (from Task 3) to clear authentication token
  - Implement navigation back to Login screen with proper activity stack clearing
  - Use Intent flags to prevent back navigation to authenticated screens
  - Test complete logout flow and token clearing
  - _Requirements: 6.4, 6.5_
  - _Test Strategy: From Profile screen, click Logout. Verify token is cleared (check via logs) and app navigates to Login screen. Ensure pressing back does not return to Profile/Home screen._

- [ ] 23. Integrate Login API
  - Add networking library dependency (Retrofit/Volley) to build.gradle
  - Create ApiClient class with base URL and HTTP client configuration
  - Implement AuthApiService interface with login endpoint
  - Create login request/response models
  - Integrate API call in Login screen with proper error handling
  - Save authentication token using TokenManager on successful login
  - Navigate to Home screen on successful authentication
  - Display appropriate error messages for failed login attempts
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [ ] 24. Integrate Registration API
  - Extend AuthApiService interface with registration endpoint
  - Create registration request/response models
  - Integrate API call in Registration screen with validation
  - Handle successful registration with navigation to Login or Home
  - Implement proper error handling and user feedback for registration failures
  - Test registration flow with valid and invalid data
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5_

- [ ] 25. Integrate Home/List API (Fetch Nearby Lots)
  - Create ParkingApiService interface with nearby lots endpoint
  - Implement ParkingLot model class with all required fields
  - Create ParkingRepository class following repository pattern
  - Integrate location services to get current coordinates for API call
  - Update ParkingLotAdapter to display real API data instead of mock data
  - Implement proper error handling for API failures
  - Add loading indicators during API calls
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 2.1, 2.2_

- [ ] 26. Integrate Parking Lot Details API
  - Extend ParkingApiService with lot details endpoint
  - Implement API call when navigating to Parking Lot Details screen
  - Update Details screen UI elements with real API data
  - Handle loading states and error conditions
  - Display actual availability, rates, hours, and reviews data
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 4.1_

- [ ] 27. Integrate Booking API
  - Create BookingApiService interface with booking creation endpoint
  - Implement Booking model and BookingRequest classes
  - Add API call integration in Booking screen "Confirm Booking" action
  - Handle successful booking response with navigation to Confirmation
  - Display actual booking details received from API in Confirmation screen
  - Implement proper error handling for booking failures
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 4.4, 4.5_

- [ ] 28. Integrate My Bookings API
  - Extend BookingApiService with user bookings endpoints (upcoming/past)
  - Update BookingAdapter to display real booking data from API
  - Implement API calls when My Bookings screen loads
  - Update both Upcoming and Past tabs with fetched data
  - Handle empty states and error conditions appropriately
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 5.1, 5.2_

- [ ] 29. Integrate Profile API
  - Create UserApiService interface with profile endpoints
  - Implement User model class with profile information
  - Add API call to fetch user profile data when Profile screen loads
  - Update Profile screen TextViews with real user data from API
  - Implement "Edit Profile" functionality with PUT request for updates
  - Handle API errors and loading states appropriately
  - _Requirements: 7.1, 7.2, 7.3, 7.4, 7.5, 6.3_

- [ ] 30. Implement Embedded Google Map View and Display Pins
  - Add Google Maps SDK dependency to build.gradle
  - Replace map placeholder FrameLayout with SupportMapFragment
  - Implement OnMapReadyCallback to obtain GoogleMap object
  - Create map markers for parking lots with color coding based on availability
  - Implement marker click listeners for navigation to lot details
  - Center map on user location or fetched parking lot locations
  - Handle map loading states and errors
  - _Requirements: 2.1, 2.2, 2.3, 2.4, 2.5_

- [ ] 31. Implement Search Functionality
  - Add TextWatcher or OnQueryTextListener to search bar
  - Implement location-based search API integration
  - Update map view and parking list with search results
  - Handle search query validation and empty results
  - Implement search result filtering and sorting
  - _Requirements: 3.1, 3.2_

- [ ] 32. Implement 'Use Current Location' Feature
  - Add OnClickListener to "Use Current Location" button
  - Implement location permission request handling
  - Integrate FusedLocationProviderClient for current location
  - Center map on obtained current location
  - Trigger nearby parking lots API call with current coordinates
  - Handle location services disabled and permission denied scenarios
  - _Requirements: 3.3, 3.4, 3.5, 3.6_

- [ ] 33. Implement Slot Availability Visual Indicators
  - Update ParkingLotAdapter to set availability badge colors based on API data
  - Implement color coding logic (green/yellow/red for available/limited/full)
  - Update Google Map markers with appropriate colors based on availability
  - Ensure consistent color scheme across list items and map pins
  - Add availability status calculation methods in ParkingLot model
  - _Requirements: 2.3, 2.4, 3.5, 8.2_

- [ ] 34. Implement Distance Calculation and Display
  - Integrate Android Location.distanceBetween() method for distance calculation
  - Calculate distance between user's current location and each parking lot
  - Update ParkingLotAdapter to display calculated distances in list items
  - Format distance display appropriately (meters/kilometers)
  - Handle cases where user location is not available
  - _Requirements: 3.2, 3.6_

- [ ] 35. Implement Booking Cancellation
  - Add "Cancel" button/option to upcoming booking items in My Bookings list
  - Extend BookingApiService with cancellation endpoint
  - Implement cancellation API call with booking ID
  - Update My Bookings list upon successful cancellation
  - Add confirmation dialog before cancellation
  - Handle cancellation restrictions and error scenarios
  - _Requirements: 5.5_

- [ ] 36. Integrate Payment Flow
  - Determine payment integration strategy (web view, SDK, or direct card input)
  - Connect booking confirmation flow to payment initiation
  - Implement payment screen integration or external payment provider redirect
  - Handle payment success and failure callbacks
  - Update booking status based on payment results
  - Test payment flow in development environment
  - _Requirements: 6.1, 6.2_

- [ ] 37. Implement Rate & Review Submission
  - Add "Rate & Review" action to past booking items in My Bookings
  - Extend API service with rate and review submission endpoint
  - Implement navigation from My Bookings to Rate & Review screen with booking data
  - Add OnClickListener to "Submit" button in Rate & Review screen
  - Collect rating, review text, and image data for API submission
  - Handle review submission success and error responses
  - _Requirements: 8.1, 8.4_