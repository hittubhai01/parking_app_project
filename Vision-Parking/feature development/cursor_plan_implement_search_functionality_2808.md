**Cursor**

I'll analyze the requirements and create a comprehensive step-by-step plan for implementing the search functionality in the Android app. Let me first examine the current codebase structure and the requirements.


Now let me check the current HomeActivity implementation to understand the existing structure:


Based on my analysis of the requirements and current codebase, here's my comprehensive step-by-step plan for implementing the search functionality in the Android app:

## Step-by-Step Implementation Plan for Search Functionality

### **Phase 1: Dependencies and Setup**

#### Step 1: Add Google Places API Dependencies
- Add Google Places API dependency to `build.gradle.kts`:
  ```kotlin
  implementation("com.google.android.libraries.places:places:3.3.0")
  ```
- Update the existing Google Maps dependency if needed

#### Step 2: Update AndroidManifest.xml
- Add Places API key to the manifest (already has Maps API key)
- Add necessary permissions for Places API

### **Phase 2: UI Layout Updates**

#### Step 3: Modify activity_home.xml
- Replace the current search bar with Google Places Autocomplete Fragment
- Ensure the search bar is positioned above the map fragment
- Maintain the existing styling and Material Design components

#### Step 4: Update HomeActivity.java Structure
- Add necessary imports for Places API
- Add member variables for Places API components
- Initialize Places API in onCreate()

### **Phase 3: Google Places Integration**

#### Step 5: Implement Places Autocomplete
- Initialize Places API client
- Set up AutocompleteSupportFragment
- Configure the fragment with proper styling and behavior
- Handle place selection callbacks

#### Step 6: Implement Location Search Functionality
- When user selects a place, extract LatLng coordinates
- Move Google Map camera to the selected location
- Update map center and zoom level appropriately

### **Phase 4: Parking Lot Markers Implementation**

#### Step 7: Create Parking Lot Data Model
- Create a `ParkingLot` class with properties:
  - Location (LatLng)
  - Name
  - Fee
  - Availability status
  - Color indicator (red/green/yellow)

#### Step 8: Implement Marker Display Logic
- Add method to plot parking lots on the map
- Use different colored markers based on availability:
  - Red: No available spots
  - Green: Available spots
  - Yellow: Limited availability
- Display parking fee as text label above each marker

#### Step 9: Create Marker Customization
- Design custom marker icons for different parking lot states
- Implement text labels showing parking fees
- Ensure markers are properly positioned and visible

### **Phase 5: Data Integration**

#### Step 10: Mock Data Implementation
- Create sample parking lot data for testing
- Implement methods to fetch nearby parking lots
- Add logic to filter parking lots based on search location

#### Step 11: API Integration (Future Enhancement)
- Prepare structure for backend API integration
- Add methods to fetch real parking lot data
- Implement error handling for API calls

### **Phase 6: User Experience Enhancements**

#### Step 12: Search Bar Improvements
- Add proper hint text and styling
- Implement search suggestions
- Add loading states during search
- Handle empty search results

#### Step 13: Map Interaction
- Ensure smooth camera transitions
- Add marker click handlers
- Implement info windows for parking lot details
- Add zoom controls and map type options

### **Phase 7: Testing and Refinement**

#### Step 14: Unit Testing
- Test Places API integration
- Test marker placement and display
- Test search functionality
- Test map camera movements

#### Step 15: UI/UX Testing
- Test on different screen sizes
- Verify Material Design compliance
- Test accessibility features
- Optimize performance

### **Phase 8: Code Organization and Documentation**

#### Step 16: Code Refactoring
- Extract search functionality into separate classes
- Create utility classes for map operations
- Implement proper error handling
- Add comprehensive logging

#### Step 17: Documentation
- Add JavaDoc comments
- Update README with new features
- Create user guide for search functionality

### **Detailed Implementation Steps:**

#### **Step 1: Dependencies Update**
```kotlin
// In build.gradle.kts
dependencies {
    // Existing dependencies...
    implementation("com.google.android.libraries.places:places:3.3.0")
}
```

#### **Step 2: Layout Modification**
- Replace the current EditText search with Places Autocomplete Fragment
- Maintain existing Material Design styling
- Ensure proper positioning above the map

#### **Step 3: Places API Integration**
```java
// In HomeActivity.java
private PlacesClient placesClient;
private AutocompleteSupportFragment autocompleteFragment;

private void initializePlaces() {
    Places.initialize(this, getString(R.string.google_maps_key));
    placesClient = Places.createClient(this);
}
```

#### **Step 4: Marker Implementation**
```java
// Create parking lot markers with different colors
private void addParkingLotMarkers() {
    // Red markers for unavailable lots
    // Green markers for available lots  
    // Yellow markers for limited availability
    // Add fee labels above each marker
}
```

#### **Step 5: Search Integration**
```java
// Handle place selection and map movement
private void onPlaceSelected(Place place) {
    LatLng location = place.getLatLng();
    if (location != null) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        // Update nearby parking lots based on new location
        updateNearbyParkingLots(location);
    }
}
```

### **Key Considerations:**

1. **Performance**: Implement efficient marker management to avoid memory issues
2. **User Experience**: Ensure smooth transitions and responsive UI
3. **Error Handling**: Proper handling of API failures and network issues
4. **Accessibility**: Ensure the search functionality is accessible to all users
5. **Testing**: Comprehensive testing on different devices and Android versions

This plan provides a structured approach to implementing the search functionality while maintaining the existing app architecture and ensuring a smooth user experience.

```plaintext
  implementation("com.google.android.libraries.places:places:3.3.0")
```

```plaintext
// In build.gradle.kts
dependencies {
    // Existing dependencies...
    implementation("com.google.android.libraries.places:places:3.3.0")
}
```

```java
// In HomeActivity.java
private PlacesClient placesClient;
private AutocompleteSupportFragment autocompleteFragment;

private void initializePlaces() {
    Places.initialize(this, getString(R.string.google_maps_key));
    placesClient = Places.createClient(this);
}
```

```java
// Create parking lot markers with different colors
private void addParkingLotMarkers() {
    // Red markers for unavailable lots
    // Green markers for available lots  
    // Yellow markers for limited availability
    // Add fee labels above each marker
}
```

```java
// Handle place selection and map movement
private void onPlaceSelected(Place place) {
    LatLng location = place.getLatLng();
    if (location != null) {
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
        // Update nearby parking lots based on new location
        updateNearbyParkingLots(location);
    }
}
```

---

**Cursor**

You're absolutely right! Let me create a comprehensive, consolidated step-by-step implementation plan that combines the best of both approaches and ensures consistency with the original requirements. Here's the refined plan:

## **Comprehensive Step-by-Step Implementation Plan for Search Functionality**

### **Phase 1: Dependencies and Setup**

#### **Step 1: Add Google Places API Dependencies**
```kotlin
// In app/build.gradle.kts
dependencies {
    // Existing dependencies...
    implementation("com.google.android.libraries.places:places:3.3.0")
    // Ensure Google Maps is up to date
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}
```

#### **Step 2: Update AndroidManifest.xml**
```xml
<!-- Add Places API key (can use same key as Maps) -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_maps_key" />

<!-- Ensure necessary permissions are present -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

### **Phase 2: UI Layout Updates**

#### **Step 3: Modify activity_home.xml**
```xml
<!-- Replace the current search card with AutocompleteSupportFragment -->
<fragment
    android:id="@+id/autocomplete_fragment"
    android:name="com.google.android.libraries.places.widget.AutocompleteSupportFragment"
    android:layout_width="0dp"
    android:layout_height="48dp"
    app:layout_constraintTop_toBottomOf="@id/topAppBar"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    android:layout_marginStart="16dp"
    android:layout_marginEnd="16dp" />
```

#### **Step 4: Update HomeActivity.java Structure**
```java
// Add necessary imports
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

// Add member variables
private PlacesClient placesClient;
private AutocompleteSupportFragment autocompleteFragment;
private MarkerGenerator markerGenerator;
private List<Marker> parkingMarkers = new ArrayList<>();
```

### **Phase 3: Google Places Integration**

#### **Step 5: Implement Places Autocomplete**
```java
// Initialize Places API in onCreate()
private void initializePlaces() {
    if (!Places.isInitialized()) {
        Places.initialize(this, getString(R.string.google_maps_key));
    }
    placesClient = Places.createClient(this);
}

// Setup AutocompleteSupportFragment
private void setupPlacesAutocomplete() {
    autocompleteFragment = (AutocompleteSupportFragment)
        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    
    autocompleteFragment.setPlaceFields(Arrays.asList(
        Place.Field.ID, 
        Place.Field.NAME, 
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS
    ));
    
    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(@NonNull Place place) {
            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                onSearchLocationSelected(latLng);
            }
        }
        
        @Override
        public void onError(@NonNull Status status) {
            Log.e(TAG, "Error: " + status);
            Toast.makeText(HomeActivity.this, "Search failed: " + status.getStatusMessage(), 
                          Toast.LENGTH_SHORT).show();
        }
    });
}
```

#### **Step 6: Implement Location Search Functionality**
```java
// Handle place selection and move map camera
private void onSearchLocationSelected(LatLng location) {
    // Move Google Map camera to the selected location
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    
    // Fetch nearby parking lots
    fetchNearbyParking(location.latitude, location.longitude);
}
```

### **Phase 4: Get Nearby Parking Lots**

#### **Step 6: Create Parking Lot Data Model
```java
// Create new file: models/ParkingLot.java
public class ParkingLot {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String carFee;
    private String twoWheelerFee;
    private int availableCarSlots;
    private int totalCarSlots;
    private int availableTwoWheelerSlots;
    private int totalTwoWheelerSlots;
    private String paymentMode;
    
    // Constructor, getters, setters...
    
    public String getDisplayFee() {
        if (carFee != null && !carFee.isEmpty()) {
            return carFee;
        }
        return "Free";
    }
    
    public String getAvailabilityStatus() {
        int totalSlots = totalCarSlots + totalTwoWheelerSlots;
        int availableSlots = availableCarSlots + availableTwoWheelerSlots;
        
        if (availableSlots == 0) return "RED";
        if (availableSlots < totalSlots * 0.3) return "YELLOW";
        return "GREEN";
    }
}
```

#### **Step 7: Create JSON Data from SQL
```java
// Create new file: utils/ParkingDataLoader.java
public class ParkingDataLoader {
    private static final String PARKING_DATA_JSON = "parking_lots.json";
    
    public static List<ParkingLot> loadParkingLots(Context context) {
        // Load from assets/parking_lots.json
        // Convert SQL data to JSON format
    }
    
    public static List<ParkingLot> getNearbyParkingLots(List<ParkingLot> allLots, 
                                                       double lat, double lng, double radiusKm) {
        List<ParkingLot> nearbyLots = new ArrayList<>();
        for (ParkingLot lot : allLots) {
            double distance = calculateDistance(lat, lng, lot.getLatitude(), lot.getLongitude());
            if (distance <= radiusKm) {
                nearbyLots.add(lot);
            }
        }
        return nearbyLots;
    }
    
    private static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        // Haversine formula implementation
    }
}
```

#### **Step 8: Create JSON File from SQL Data
```json
// Create assets/parking_lots.json
[
  {
    "id": 1,
    "name": "Jahangirpuri - Metro Authorised Parking",
    "latitude": 28.72542191,
    "longitude": 77.16333008,
    "carFee": "20 up to 6 hours, 30 for 12 hours",
    "twoWheelerFee": "10 up to 6 hours, 15 up to 12",
    "availableCarSlots": 200,
    "totalCarSlots": 200,
    "availableTwoWheelerSlots": 500,
    "totalTwoWheelerSlots": 500,
    "paymentMode": "Cash"
  },
  // ... more entries
]
```

#### **Step 9: Implement fetchNearbyParking Method**
```java
// In HomeActivity.java
private void fetchNearbyParking(double lat, double lng) {
    // Try API first (when backend is available)
    fetchParkingFromAPI(lat, lng);
    
    // Fallback to local data from SQL
    List<ParkingLot> allLots = ParkingDataLoader.loadParkingLotsFromSQL();
    List<ParkingLot> nearbyLots = ParkingDataLoader.getNearbyParkingLots(
        allLots, lat, lng, 5.0); // 5km radius
    
    displayParkingLotsOnMap(nearbyLots);
}

private void fetchParkingFromAPI(double lat, double lng) {
    // Future implementation for GET /parking?lat=&lng=&radius=
    // This will be implemented when backend is available
    Log.d(TAG, "API integration not yet implemented, using local data");
}
```

### **Phase 5: Custom Marker Implementation**

#### **Step 10: Create Custom Marker Generator**
```java
// Create utils/MarkerGenerator.java
public class MarkerGenerator {
    private Context context;
    private IconGenerator iconGenerator;
    
    public MarkerGenerator(Context context) {
        this.context = context;
        iconGenerator = new IconGenerator(context);
    }
    
    public Bitmap createParkingMarker(ParkingLot parkingLot) {
        // Create custom layout for marker
        View markerView = LayoutInflater.from(context)
            .inflate(R.layout.custom_marker_layout, null);
        
        TextView priceText = markerView.findViewById(R.id.price_text);
        ImageView pinIcon = markerView.findViewById(R.id.pin_icon);
        
        // Set price text
        priceText.setText(parkingLot.getDisplayFee());
        
        // Set pin color based on availability
        String status = parkingLot.getAvailabilityStatus();
        int pinColor = getPinColor(status);
        pinIcon.setColorFilter(pinColor);
        
        // Configure icon generator
        iconGenerator.setContentView(markerView);
        iconGenerator.setBackground(null);
        
        return iconGenerator.makeIcon();
    }
    
    private int getPinColor(String status) {
        switch (status) {
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "RED": return Color.RED;
            default: return Color.GRAY;
        }
    }
}
```

#### **Step 11: Create Custom Marker Layout**
```xml
<!-- Create res/layout/custom_marker_layout.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center">
    
    <TextView
        android:id="@+id/price_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/price_background"
        android:padding="6dp"
        android:textSize="10sp"
        android:textColor="@android:color/white"
        android:text="₹20/hr"
        android:maxLines="2"
        android:ellipsize="end" />
    
    <ImageView
        android:id="@+id/pin_icon"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_parking_pin"
        android:layout_marginTop="2dp" />
        
</LinearLayout>
```

#### **Step 12: Create Pin Icon and Background Resources**
```xml
<!-- Create res/drawable/ic_parking_pin.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7,-7.75 7,-13c0,-3.87 -3.13,-7 -7,-7zM12,11.5c-1.38,0 -2.5,-1.12 -2.5,-2.5s1.12,-2.5 2.5,-2.5 2.5,1.12 2.5,2.5 -1.12,2.5 -2.5,2.5z"/>
</vector>

<!-- Create res/drawable/price_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#80000000" />
    <corners android:radius="6dp" />
    <stroke android:width="1dp" android:color="#FFFFFF" />
</shape>
```

### **Phase 6: Map Integration and Marker Display**

#### **Step 13: Display Markers on Map**
```java
// In HomeActivity.java
private void displayParkingLotsOnMap(List<ParkingLot> parkingLots) {
    // Clear existing markers
    clearParkingMarkers();
    
    markerGenerator = new MarkerGenerator(this);
    
    for (ParkingLot lot : parkingLots) {
        LatLng position = new LatLng(lot.getLatitude(), lot.getLongitude());
        Bitmap customMarker = markerGenerator.createParkingMarker(lot);
        
        Marker marker = mMap.addMarker(new MarkerOptions()
            .position(position)
            .icon(BitmapDescriptorFactory.fromBitmap(customMarker))
            .title(lot.getName())
            .snippet(lot.getDisplayFee()));
        
        // Store reference for later use
        marker.setTag(lot);
        parkingMarkers.add(marker);
    }
    
    Log.d(TAG, "Displayed " + parkingLots.size() + " parking lots on map");
}

private void clearParkingMarkers() {
    for (Marker marker : parkingMarkers) {
        marker.remove();
    }
    parkingMarkers.clear();
}
```

#### **Step 14: Add Marker Click Handler**
```java
// In onMapReady method
mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
    @Override
    public boolean onMarkerClick(Marker marker) {
        ParkingLot parkingLot = (ParkingLot) marker.getTag();
        if (parkingLot != null) {
            showParkingLotDetails(parkingLot);
        }
        return true;
    }
});

private void showParkingLotDetails(ParkingLot parkingLot) {
    // Show detailed information about the parking lot
    String details = "Name: " + parkingLot.getName() + "\n" +
                    "Fee: " + parkingLot.getDisplayFee() + "\n" +
                    "Available Car Slots: " + parkingLot.getAvailableCarSlots() + 
                    "/" + parkingLot.getTotalCarSlots() + "\n" +
                    "Available Two-Wheeler Slots: " + parkingLot.getAvailableTwoWheelerSlots() + 
                    "/" + parkingLot.getTotalTwoWheelerSlots();
    
    // For now, show in Toast. Later can be expanded to show in dialog or navigate to details screen
    Toast.makeText(this, details, Toast.LENGTH_LONG).show();
}
```

### **Phase 7: Integration and Testing**

#### **Step 15: Update HomeActivity onCreate**
```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    
    // Existing initialization code...
    
    // Initialize Places API
    initializePlaces();
    
    // Setup Places Autocomplete
    setupPlacesAutocomplete();
    
    // Initialize map
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.mapFragment);
    if (mapFragment != null) {
        mapFragment.getMapAsync(this);
    }
}
```

#### **Step 16: Add Error Handling and Loading States**
```java
// Add loading states and error handling
private void showLoadingState() {
    // Show loading indicator
    Toast.makeText(this, "Searching for parking lots...", Toast.LENGTH_SHORT).show();
}

private void hideLoadingState() {
    // Hide loading indicator
}

private void handleSearchError(String error) {
    Toast.makeText(this, "Search failed: " + error, Toast.LENGTH_LONG).show();
    Log.e(TAG, "Search error: " + error);
}
```

### **Phase 8: Future Enhancements**

#### **Step 17: API Integration Preparation**
```java
// Prepare for future API integration
private void fetchParkingFromAPI(double lat, double lng) {
    // TODO: Implement API call to GET /parking?lat=&lng=&radius=
    // This will be implemented when backend is available
    
    // For now, use local data
    List<ParkingLot> allLots = ParkingDataLoader.loadParkingLotsFromSQL();
    List<ParkingLot> nearbyLots = ParkingDataLoader.getNearbyParkingLots(
        allLots, lat, lng, 5.0);
    displayParkingLotsOnMap(nearbyLots);
}
```

#### **Step 18: Performance Optimization**
```java
// Add performance optimizations
private void optimizeMapPerformance() {
    // Limit number of markers displayed
    if (parkingMarkers.size() > 50) {
        // Show only closest 50 parking lots
        Collections.sort(parkingLots, new Comparator<ParkingLot>() {
            @Override
            public int compare(ParkingLot p1, ParkingLot p2) {
                // Sort by distance
                return Double.compare(p1.getDistance(), p2.getDistance());
            }
        });
    }
}
```

### **Expected Outcomes:**

1. **Search Functionality**: User can type a place name and get its LatLng coordinates
2. **Nearby Parking**: App fetches nearby parking lots from local SQL data (with API fallback)
3. **Visual Markers**: Map displays colored markers with price text:
   - **GREEN**: Available parking spots
   - **YELLOW**: Limited availability  
   - **RED**: No available spots
4. **Interactive Map**: Clicking markers shows parking lot details
5. **Smooth UX**: Camera moves smoothly to selected locations

This comprehensive plan ensures consistency with the original requirements while providing a robust, scalable implementation that can easily be extended with API integration when the backend is available.

```plaintext
// In app/build.gradle.kts
dependencies {
    // Existing dependencies...
    implementation("com.google.android.libraries.places:places:3.3.0")
    // Ensure Google Maps is up to date
    implementation("com.google.android.gms:play-services-maps:18.2.0")
}
```

```xml
<!-- Add Places API key (can use same key as Maps) -->
<meta-data
    android:name="com.google.android.geo.API_KEY"
    android:value="@string/google_maps_key" />

<!-- Ensure necessary permissions are present -->
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

```xml
<!-- Replace the current search card with AutocompleteSupportFragment -->
<fragment
    android:id="@+id/autocomplete_fragment"
    android:name="com.google.android.libraries.places.widget.AutocompleteSupportFragment"
    android:layout_width="0dp"
    android:layout_height="48dp"
    app:layout_constraintTop_toBottomOf="@id/topAppBar"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintEnd_toEndOf="parent"
    android:layout_marginStart="16dp"
    android:layout_marginEnd="16dp" />
```

```java
// Add necessary imports
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.ui.IconGenerator;

// Add member variables
private PlacesClient placesClient;
private AutocompleteSupportFragment autocompleteFragment;
private MarkerGenerator markerGenerator;
private List<Marker> parkingMarkers = new ArrayList<>();
```

```java
// Initialize Places API in onCreate()
private void initializePlaces() {
    if (!Places.isInitialized()) {
        Places.initialize(this, getString(R.string.google_maps_key));
    }
    placesClient = Places.createClient(this);
}

// Setup AutocompleteSupportFragment
private void setupPlacesAutocomplete() {
    autocompleteFragment = (AutocompleteSupportFragment)
        getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    
    autocompleteFragment.setPlaceFields(Arrays.asList(
        Place.Field.ID, 
        Place.Field.NAME, 
        Place.Field.LAT_LNG,
        Place.Field.ADDRESS
    ));
    
    autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
        @Override
        public void onPlaceSelected(@NonNull Place place) {
            LatLng latLng = place.getLatLng();
            if (latLng != null) {
                onSearchLocationSelected(latLng);
            }
        }
        
        @Override
        public void onError(@NonNull Status status) {
            Log.e(TAG, "Error: " + status);
            Toast.makeText(HomeActivity.this, "Search failed: " + status.getStatusMessage(), 
                          Toast.LENGTH_SHORT).show();
        }
    });
}
```

```java
// Handle place selection and move map camera
private void onSearchLocationSelected(LatLng location) {
    // Move Google Map camera to the selected location
    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 15f));
    
    // Fetch nearby parking lots
    fetchNearbyParking(location.latitude, location.longitude);
}
```

```java
// Create models/ParkingLot.java
public class ParkingLot {
    private int id;
    private String name;
    private double latitude;
    private double longitude;
    private String carFee;
    private String twoWheelerFee;
    private int availableCarSlots;
    private int totalCarSlots;
    private int availableTwoWheelerSlots;
    private int totalTwoWheelerSlots;
    private String paymentMode;
    private String address;
    private String landmark;
    
    // Constructor
    public ParkingLot(int id, String name, double latitude, double longitude, 
                     String carFee, String twoWheelerFee, int availableCarSlots, 
                     int totalCarSlots, int availableTwoWheelerSlots, 
                     int totalTwoWheelerSlots, String paymentMode) {
        this.id = id;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.carFee = carFee;
        this.twoWheelerFee = twoWheelerFee;
        this.availableCarSlots = availableCarSlots;
        this.totalCarSlots = totalCarSlots;
        this.availableTwoWheelerSlots = availableTwoWheelerSlots;
        this.totalTwoWheelerSlots = totalTwoWheelerSlots;
        this.paymentMode = paymentMode;
    }
    
    // Getters and setters...
    
    public String getDisplayFee() {
        if (carFee != null && !carFee.isEmpty() && !carFee.equals("Free")) {
            return carFee;
        }
        return "Free";
    }
    
    public String getAvailabilityStatus() {
        int totalSlots = totalCarSlots + totalTwoWheelerSlots;
        int availableSlots = availableCarSlots + availableTwoWheelerSlots;
        
        if (availableSlots == 0) return "RED";
        if (availableSlots < totalSlots * 0.3) return "YELLOW";
        return "GREEN";
    }
}
```

```java
// Create utils/ParkingDataLoader.java
public class ParkingDataLoader {
    private static final String PARKING_DATA_JSON = "parking_lots.json";
    
    public static List<ParkingLot> loadParkingLots(Context context) {
        // Load from assets/parking_lots.json
        // Convert SQL data to JSON format
    }
    
    public static List<ParkingLot> getNearbyParkingLots(List<ParkingLot> allLots, 
                                                       double lat, double lng, double radiusKm) {
        List<ParkingLot> nearbyLots = new ArrayList<>();
        for (ParkingLot lot : allLots) {
            double distance = calculateDistance(lat, lng, lot.getLatitude(), lot.getLongitude());
            if (distance <= radiusKm) {
                nearbyLots.add(lot);
            }
        }
        return nearbyLots;
    }
    
    private static double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }
}
```
```json
// Create assets/parking_lots.json
[
  {
    "id": 1,
    "name": "Jahangirpuri - Metro Authorised Parking",
    "latitude": 28.72542191,
    "longitude": 77.16333008,
    "carFee": "20 up to 6 hours, 30 for 12 hours",
    "twoWheelerFee": "10 up to 6 hours, 15 up to 12",
    "availableCarSlots": 200,
    "totalCarSlots": 200,
    "availableTwoWheelerSlots": 500,
    "totalTwoWheelerSlots": 500,
    "paymentMode": "Cash"
  },
  // ... more entries
]
```

```java
// In HomeActivity.java
private void fetchNearbyParking(double lat, double lng) {
    // Try API first (when backend is available)
    fetchParkingFromAPI(lat, lng);
    
    // Fallback to local data from SQL
    List<ParkingLot> allLots = ParkingDataLoader.loadParkingLotsFromSQL();
    List<ParkingLot> nearbyLots = ParkingDataLoader.getNearbyParkingLots(
        allLots, lat, lng, 5.0); // 5km radius
    
    displayParkingLotsOnMap(nearbyLots);
}

private void fetchParkingFromAPI(double lat, double lng) {
    // Future implementation for GET /parking?lat=&lng=&radius=
    // This will be implemented when backend is available
    Log.d(TAG, "API integration not yet implemented, using local data");
}
```

```java
// Create utils/MarkerGenerator.java
public class MarkerGenerator {
    private Context context;
    private IconGenerator iconGenerator;
    
    public MarkerGenerator(Context context) {
        this.context = context;
        iconGenerator = new IconGenerator(context);
    }
    
    public Bitmap createParkingMarker(ParkingLot parkingLot) {
        // Create custom layout for marker
        View markerView = LayoutInflater.from(context)
            .inflate(R.layout.custom_marker_layout, null);
        
        TextView priceText = markerView.findViewById(R.id.price_text);
        ImageView pinIcon = markerView.findViewById(R.id.pin_icon);
        
        // Set price text
        priceText.setText(parkingLot.getDisplayFee());
        
        // Set pin color based on availability
        String status = parkingLot.getAvailabilityStatus();
        int pinColor = getPinColor(status);
        pinIcon.setColorFilter(pinColor);
        
        // Configure icon generator
        iconGenerator.setContentView(markerView);
        iconGenerator.setBackground(null);
        
        return iconGenerator.makeIcon();
    }
    
    private int getPinColor(String status) {
        switch (status) {
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "RED": return Color.RED;
            default: return Color.GRAY;
        }
    }
}
```

```xml
<!-- Create res/layout/custom_marker_layout.xml -->
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center">
    
    <TextView
        android:id="@+id/price_text"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:background="@drawable/price_background"
        android:padding="6dp"
        android:textSize="10sp"
        android:textColor="@android:color/white"
        android:text="₹20/hr"
        android:maxLines="2"
        android:ellipsize="end" />
    
    <ImageView
        android:id="@+id/pin_icon"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@drawable/ic_parking_pin"
        android:layout_marginTop="2dp" />
        
</LinearLayout>
```

```xml
<!-- Create res/drawable/ic_parking_pin.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24">
    <path
        android:fillColor="@android:color/white"
        android:pathData="M12,2C8.13,2 5,5.13 5,9c0,5.25 7,13 7,13s7,-7.75 7,-13c0,-3.87 -3.13,-7 -7,-7zM12,11.5c-1.38,0 -2.5,-1.12 -2.5,-2.5s1.12,-2.5 2.5,-2.5 2.5,1.12 2.5,2.5 -1.12,2.5 -2.5,2.5z"/>
</vector>

<!-- Create res/drawable/price_background.xml -->
<shape xmlns:android="http://schemas.android.com/apk/res/android"
    android:shape="rectangle">
    <solid android:color="#80000000" />
    <corners android:radius="6dp" />
    <stroke android:width="1dp" android:color="#FFFFFF" />
</shape>
```

```java
// In HomeActivity.java
private void displayParkingLotsOnMap(List<ParkingLot> parkingLots) {
    // Clear existing markers
    clearParkingMarkers();
    
    markerGenerator = new MarkerGenerator(this);
    
    for (ParkingLot lot : parkingLots) {
        LatLng position = new LatLng(lot.getLatitude(), lot.getLongitude());
        Bitmap customMarker = markerGenerator.createParkingMarker(lot);
        
        Marker marker = mMap.addMarker(new MarkerOptions()
            .position(position)
            .icon(BitmapDescriptorFactory.fromBitmap(customMarker))
            .title(lot.getName())
            .snippet(lot.getDisplayFee()));
        
        // Store reference for later use
        marker.setTag(lot);
        parkingMarkers.add(marker);
    }
    
    Log.d(TAG, "Displayed " + parkingLots.size() + " parking lots on map");
}

private void clearParkingMarkers() {
    for (Marker marker : parkingMarkers) {
        marker.remove();
    }
    parkingMarkers.clear();
}
```

```java
// In onMapReady method
mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
    @Override
    public boolean onMarkerClick(Marker marker) {
        ParkingLot parkingLot = (ParkingLot) marker.getTag();
        if (parkingLot != null) {
            showParkingLotDetails(parkingLot);
        }
        return true;
    }
});

private void showParkingLotDetails(ParkingLot parkingLot) {
    // Show detailed information about the parking lot
    String details = "Name: " + parkingLot.getName() + "\n" +
                    "Fee: " + parkingLot.getDisplayFee() + "\n" +
                    "Available Car Slots: " + parkingLot.getAvailableCarSlots() + 
                    "/" + parkingLot.getTotalCarSlots() + "\n" +
                    "Available Two-Wheeler Slots: " + parkingLot.getAvailableTwoWheelerSlots() + 
                    "/" + parkingLot.getTotalTwoWheelerSlots();
    
    // For now, show in Toast. Later can be expanded to show in dialog or navigate to details screen
    Toast.makeText(this, details, Toast.LENGTH_LONG).show();
}
```

```java
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
    
    // Existing initialization code...
    
    // Initialize Places API
    initializePlaces();
    
    // Setup Places Autocomplete
    setupPlacesAutocomplete();
    
    // Initialize map
    SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
        .findFragmentById(R.id.mapFragment);
    if (mapFragment != null) {
        mapFragment.getMapAsync(this);
    }
}
```

```java
// Add loading states and error handling
private void showLoadingState() {
    // Show loading indicator
    Toast.makeText(this, "Searching for parking lots...", Toast.LENGTH_SHORT).show();
}

private void hideLoadingState() {
    // Hide loading indicator
}

private void handleSearchError(String error) {
    Toast.makeText(this, "Search failed: " + error, Toast.LENGTH_LONG).show();
    Log.e(TAG, "Search error: " + error);
}
```

```java
// Prepare for future API integration
private void fetchParkingFromAPI(double lat, double lng) {
    // TODO: Implement API call to GET /parking?lat=&lng=&radius=
    // This will be implemented when backend is available
    
    // For now, use local data
    List<ParkingLot> allLots = ParkingDataLoader.loadParkingLotsFromSQL();
    List<ParkingLot> nearbyLots = ParkingDataLoader.getNearbyParkingLots(
        allLots, lat, lng, 5.0);
    displayParkingLotsOnMap(nearbyLots);
}
```

```java
// Add performance optimizations
private void optimizeMapPerformance() {
    // Limit number of markers displayed
    if (parkingMarkers.size() > 50) {
        // Show only closest 50 parking lots
        Collections.sort(parkingLots, new Comparator<ParkingLot>() {
            @Override
            public int compare(ParkingLot p1, ParkingLot p2) {
                // Sort by distance
                return Double.compare(p1.getDistance(), p2.getDistance());
            }
        });
    }
}
```

---



